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
import { FiUserPlus } from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import { toaster } from "../../../components/ui/toaster";
import {
  formatZipCode,
  hasErrors,
  initialRegistrationValues,
  onlyDigits,
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

type RegistrationFormData = {
  fullName: string;
  email: string;
  password: string;
  confirmPassword: string;
  address: {
    zipCode: string;
    state: string;
    city: string;
    district: string;
    street: string;
    complement: string;
    number: string;
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
  const zipCodeRequestRef = useRef<AbortController | null>(null);
  const numberInputRef = useRef<HTMLInputElement>(null);

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
      // Keep the lookup error visible instead of overwriting it on blur.
      if (field === "zipCode" && isFetchingAddress) return;
      if (
        field === "zipCode" &&
        (errors.zipCode === validationMessages.zipCodeNotFound ||
          errors.zipCode === validationMessages.zipCodeUnavailable)
      ) {
        return;
      }
      setFieldError(field, validateField(field, values));
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
    }
    setStep(target);
  }

  function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();

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

    const data: RegistrationFormData = {
      fullName: values.fullName.trim(),
      email: values.email.trim(),
      password: values.password,
      confirmPassword: values.confirmPassword,
      address: {
        zipCode: values.zipCode,
        state: values.state.trim(),
        city: values.city.trim(),
        district: values.district.trim(),
        street: values.street.trim(),
        complement: values.complement.trim(),
        number: values.number.trim(),
      },
    };

    // TODO: enviar `data` ao backend quando a API de cadastro existir e só
    // mostrar o sucesso/redirecionar depois da resposta.
    console.log(data);

    toaster.create({
      type: "success",
      title: "Cadastro realizado com sucesso!",
      description: "Faça login para acessar sua conta.",
    });
    navigate("/");
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
            type="button"
            aria-label="Próxima etapa"
            size="xl"
            width="1/5"
            rounded="lg"
            className={styles.submit}
            onClick={() => goToStep(1)}
          >
            →
          </Button>
        ) : (
          <Button
            type="submit"
            size="xl"
            width="full"
            rounded="lg"
            className={styles.submit}
          >
            Cadastrar
          </Button>
        )}
      </Flex>
    </chakra.form>
  );
}

export default RegistrationForms;
