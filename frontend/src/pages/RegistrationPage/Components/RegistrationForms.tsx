import {
  useEffect,
  useRef,
  useState,
  type ChangeEvent,
  type SubmitEvent,
  type ReactNode,
  type Ref,
} from "react";
import {
  Box,
  Button,
  chakra,
  Field,
  Flex,
  Heading,
  Icon,
  Input,
  InputGroup,
  Spinner,
} from "@chakra-ui/react";
import axios from "axios";
import { FiUserPlus } from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import api from "../../../api/api";
import { toaster } from "../../../components/ui/toaster";
import { paths } from "../../../routes/routes";
import {
  formatCpf,
  formatZipCode,
  hasErrors,
  initialRegistrationValues,
  onlyDigits,
  stepFields,
  validateField,
  validateStep,
  validationMessages,
  type FormErrors,
  type RegistrationField,
  type RegistrationFormValues,
} from "./registrationValidation";
import { fetchAddressByZipCode } from "./viaCep";
import styles from "./RegistrationForms.module.css";

type FormFieldProps = {
  id: RegistrationField;
  label: string;
  type?: "text" | "email" | "password";
  placeholder: string;
  value: string;
  error?: string;
  helperText?: string;
  endElement?: ReactNode;
  inputRef?: Ref<HTMLInputElement>;
  maxLength?: number;
  inputMode?: "text" | "numeric" | "email";
  autoComplete?: string;
  onChange: (event: ChangeEvent<HTMLInputElement>) => void;
  onBlur: () => void;
};

function FormField({
  id,
  label,
  type = "text",
  placeholder,
  value,
  error,
  helperText,
  endElement,
  inputRef,
  maxLength,
  inputMode,
  autoComplete,
  onChange,
  onBlur,
}: FormFieldProps) {
  const input = (
    <Input
      ref={inputRef}
      id={id}
      name={id}
      type={type}
      placeholder={placeholder}
      value={value}
      maxLength={maxLength}
      inputMode={inputMode}
      autoComplete={autoComplete}
      className={styles.input}
      onChange={onChange}
      onBlur={onBlur}
    />
  );

  return (
    <Field.Root className={styles.field} invalid={!!error}>
      <Field.Label className={styles.label} htmlFor={id}>
        {label}
      </Field.Label>
      {endElement ? (
        <InputGroup endElement={endElement}>{input}</InputGroup>
      ) : (
        input
      )}
      {helperText && !error && (
        <Field.HelperText className={styles.helperText}>
          {helperText}
        </Field.HelperText>
      )}
      <Field.ErrorText className={styles.errorText}>{error}</Field.ErrorText>
    </Field.Root>
  );
}

type CustomerRequest = {
  user: {
    name: string;
    email: string;
    password: string;
    cpf: string;
    address: {
      cep: string;
      state: string;
      city: string;
      neighborhood: string;
      street: string;
      number: string;
      complement: string;
    };
  };
};

function RegistrationForms() {
  const navigate = useNavigate();
  const [step, setStep] = useState(0);
  const [values, setValues] = useState<RegistrationFormValues>(
    initialRegistrationValues,
  );
  const [errors, setErrors] = useState<FormErrors>({});
  const [isFetchingAddress, setIsFetchingAddress] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const zipCodeRequestRef = useRef<AbortController | null>(null);
  const numberInputRef = useRef<HTMLInputElement>(null);
  const submitRequestedRef = useRef(false);

  useEffect(() => () => zipCodeRequestRef.current?.abort(), []);

  function setFieldError(field: RegistrationField, message?: string) {
    setErrors((prev) => ({ ...prev, [field]: message }));
  }

  async function lookUpZipCode(zipCode: string) {
    zipCodeRequestRef.current?.abort();
    zipCodeRequestRef.current = null;

    const digits = onlyDigits(zipCode);
    if (digits.length !== 8) {
      setIsFetchingAddress(false);
      return;
    }

    const controller = new AbortController();
    zipCodeRequestRef.current = controller;
    setIsFetchingAddress(true);

    try {
      const address = await fetchAddressByZipCode(digits, controller.signal);
      if (!address) {
        setFieldError("zipCode", validationMessages.zipCodeNotFound);
        return;
      }

      // Fill only what ViaCEP returned; fields stay editable.
      const filled = Object.fromEntries(
        Object.entries(address).filter(([, value]) => value),
      ) as Partial<RegistrationFormValues>;

      setValues((prev) => ({ ...prev, ...filled }));
      setErrors((prev) => {
        const next: FormErrors = { ...prev, zipCode: undefined };
        for (const field of Object.keys(filled) as RegistrationField[]) {
          next[field] = undefined;
        }
        return next;
      });
      numberInputRef.current?.focus();
    } catch {
      if (controller.signal.aborted) return;
      setFieldError("zipCode", validationMessages.zipCodeUnavailable);
    } finally {
      if (zipCodeRequestRef.current === controller) {
        zipCodeRequestRef.current = null;
        setIsFetchingAddress(false);
      }
    }
  }

  function handleChange(field: RegistrationField) {
    return (event: ChangeEvent<HTMLInputElement>) => {
      let value = event.target.value;
      if (field === "zipCode") value = formatZipCode(value);
      if (field === "cpf") value = formatCpf(value);
      if (field === "state") value = value.toUpperCase();

      const nextValues = { ...values, [field]: value };
      setValues(nextValues);

      // Once a field shows an error, re-check it as the user types.
      if (errors[field]) {
        setFieldError(field, validateField(field, nextValues));
      }
      if (field === "password" && errors.confirmPassword) {
        setFieldError(
          "confirmPassword",
          validateField("confirmPassword", nextValues),
        );
      }

      if (field === "zipCode" && value !== values.zipCode) {
        void lookUpZipCode(value);
      }
    };
  }

  function handleBlur(field: RegistrationField) {
    return () => {
      // Address fields are validated only when the user clicks Cadastrar.
      if (stepFields[1].includes(field)) return;

      const message = validateField(field, values);
      // "Campo obrigatório." is shown on the step arrow and on submit.
      if (message === validationMessages.required) return;
      setFieldError(field, message);
    };
  }

  function fieldProps(field: RegistrationField) {
    return {
      id: field,
      value: values[field],
      error: errors[field],
      onChange: handleChange(field),
      onBlur: handleBlur(field),
    };
  }

  function goToStep(target: number) {
    if (target === 1) {
      const stepErrors = validateStep(values, 0);
      if (hasErrors(stepErrors)) {
        setErrors((prev) => ({ ...prev, ...stepErrors }));
        return;
      }
      // Address warnings belong to Cadastrar, not to opening this step.
      setErrors((prev) => {
        const next = { ...prev };
        for (const field of stepFields[1]) {
          if (
            next[field] === validationMessages.zipCodeNotFound ||
            next[field] === validationMessages.zipCodeUnavailable
          ) {
            continue;
          }
          next[field] = undefined;
        }
        return next;
      });
    }
    setStep(target);
  }

  async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    const requested = submitRequestedRef.current;
    submitRequestedRef.current = false;
    if (!requested || isSubmitting) return;

    const personalErrors = validateStep(values, 0);
    const addressErrors = validateStep(values, 1);
    // A CEP that ViaCEP doesn't know blocks the submit; an unavailable
    // lookup doesn't, since the address was filled in manually.
    if (
      !addressErrors.zipCode &&
      errors.zipCode === validationMessages.zipCodeNotFound
    ) {
      addressErrors.zipCode = errors.zipCode;
    }
    setErrors({ ...personalErrors, ...addressErrors });

    if (hasErrors(personalErrors)) {
      setStep(0);
      return;
    }
    if (hasErrors(addressErrors)) return;

    const body: CustomerRequest = {
      user: {
        name: values.fullName.trim(),
        email: values.email.trim(),
        password: values.password,
        cpf: onlyDigits(values.cpf),
        address: {
          cep: values.zipCode,
          state: values.state.trim(),
          city: values.city.trim(),
          neighborhood: values.district.trim(),
          street: values.street.trim(),
          number: values.number.trim(),
          complement: values.complement.trim(),
        },
      },
    };

    setIsSubmitting(true);
    try {
      await api.post("/customers", body);
      toaster.create({
        type: "success",
        title: "Cadastro realizado com sucesso!",
        description: "Faça login para acessar sua conta.",
      });
      navigate(paths.login);
    } catch (error) {
      const status = axios.isAxiosError(error) ? error.response?.status : undefined;
      const apiMessage = axios.isAxiosError(error)
        ? (error.response?.data as { message?: string } | undefined)?.message
        : undefined;

      if (status === 404) {
        setFieldError("cpf", validationMessages.cpfNotFound);
        setStep(0);
      }

      toaster.create({
        type: "error",
        title: "Não foi possível concluir o cadastro",
        description:
          status === 404
            ? validationMessages.cpfNotFound
            : status === 409 && apiMessage
              ? apiMessage
              : status
                ? "Verifique os dados informados e tente novamente."
                : "Não foi possível conectar ao servidor.",
      });
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <chakra.form
      className={styles.form}
      aria-labelledby="registration-title"
      noValidate
      onSubmit={handleSubmit}
    >
      <Icon className={styles.icon} aria-hidden="true">
        <FiUserPlus />
      </Icon>

      <Box as="header" className={styles.header}>
        <Heading as="h2" id="registration-title" className={styles.title}>
          Cadastro
        </Heading>
      </Box>

      <Flex className={styles.fields} hidden={step !== 0}>
        <FormField
          {...fieldProps("fullName")}
          label="Nome completo"
          placeholder="Digite seu nome completo"
          autoComplete="name"
        />
        <FormField
          {...fieldProps("cpf")}
          label="CPF"
          placeholder="000.000.000-00"
          inputMode="numeric"
          maxLength={14}
        />
        <FormField
          {...fieldProps("email")}
          label="Email"
          type="email"
          placeholder="Digite seu email"
          inputMode="email"
          autoComplete="email"
        />
        <FormField
          {...fieldProps("password")}
          label="Senha"
          type="password"
          placeholder="Digite sua senha"
          autoComplete="new-password"
          helperText={validationMessages.passwordHint}
        />
        <FormField
          {...fieldProps("confirmPassword")}
          label="Confirmar senha"
          type="password"
          placeholder="Confirme sua senha"
          autoComplete="new-password"
        />
      </Flex>

      <Flex className={styles.fields} hidden={step !== 1}>
        <Flex className={styles.fieldsRow}>
          <FormField
            {...fieldProps("zipCode")}
            label="CEP"
            placeholder="Digite seu CEP"
            inputMode="numeric"
            autoComplete="postal-code"
            maxLength={9}
            endElement={
              isFetchingAddress ? (
                <Spinner size="sm" aria-label="Buscando endereço" />
              ) : undefined
            }
          />
          <FormField
            {...fieldProps("state")}
            label="Estado"
            placeholder="UF"
            maxLength={2}
          />
        </Flex>
        <FormField
          {...fieldProps("city")}
          label="Cidade"
          placeholder="Digite sua cidade"
        />
        <FormField
          {...fieldProps("district")}
          label="Bairro"
          placeholder="Digite seu bairro"
        />
        <FormField
          {...fieldProps("street")}
          label="Rua"
          placeholder="Digite sua rua"
        />
        <Flex className={styles.fieldsRow}>
          <FormField
            {...fieldProps("complement")}
            label="Complemento"
            placeholder="Complemento"
          />
          <FormField
            {...fieldProps("number")}
            label="Número"
            placeholder="Número"
            inputRef={numberInputRef}
          />
        </Flex>
      </Flex>

      <Flex className={styles.dots}>
        <Button
          type="button"
          aria-label="Ir para etapa 1"
          aria-current={step === 0 ? "step" : undefined}
          boxSize="2"
          minW="0"
          p="0"
          rounded="full"
          className={`${styles.dot}${step === 0 ? ` ${styles.dotActive}` : ""}`}
          onClick={() => goToStep(0)}
        />
        <Button
          type="button"
          aria-label="Ir para etapa 2"
          aria-current={step === 1 ? "step" : undefined}
          boxSize="2"
          minW="0"
          p="0"
          rounded="full"
          className={`${styles.dot}${step === 1 ? ` ${styles.dotActive}` : ""}`}
          onClick={() => goToStep(1)}
        />
      </Flex>

      <Flex className={styles.actions}>
        {step === 0 ? (
          <Button
            key="next-step"
            type="button"
            aria-label="Próxima etapa"
            size="xl"
            width="1/5"
            rounded="lg"
            className={styles.submit}
            onClick={(event) => {
              event.preventDefault();
              goToStep(1);
            }}
          >
            →
          </Button>
        ) : (
          <Button
            key="register"
            type="submit"
            size="xl"
            width="full"
            rounded="lg"
            className={styles.submit}
            loading={isSubmitting}
            onClick={() => {
              submitRequestedRef.current = true;
            }}
          >
            Cadastrar
          </Button>
        )}
      </Flex>
    </chakra.form>
  );
}

export default RegistrationForms;
