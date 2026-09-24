import { useState, type FormEvent } from "react";
import {
  Box,
  Button,
  chakra,
  Field,
  Flex,
  Heading,
  Icon,
  Input,
} from "@chakra-ui/react";
import { FiUserPlus } from "react-icons/fi";
import styles from "./RegistrationForms.module.css";

type FormFieldProps = {
  id: string;
  label: string;
  type?: "text" | "email" | "password";
  placeholder: string;
};

function FormField({ id, label, type = "text", placeholder }: FormFieldProps) {
  return (
    <Field.Root className={styles.field}>
      <Field.Label className={styles.label} htmlFor={id}>
        {label}
      </Field.Label>
      <Input
        id={id}
        name={id}
        type={type}
        placeholder={placeholder}
        className={styles.input}
      />
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
  const [step, setStep] = useState(0);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const formData = new FormData(event.currentTarget);
    const getValue = (name: string) => String(formData.get(name) ?? "").trim();

    const data: RegistrationFormData = {
      fullName: getValue("fullName"),
      email: getValue("email"),
      password: getValue("password"),
      confirmPassword: getValue("confirmPassword"),
      address: {
        zipCode: getValue("zipCode"),
        state: getValue("state"),
        city: getValue("city"),
        district: getValue("district"),
        street: getValue("street"),
        complement: getValue("complement"),
        number: getValue("number"),
      },
    };

    // TODO: enviar `data` ao backend quando a API de cadastro existir.
    console.log(data);
  }

  return (
    <chakra.form
      className={styles.form}
      aria-labelledby="registration-title"
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
          id="fullName"
          label="Nome completo"
          placeholder="Digite seu nome completo"
        />
        <FormField
          id="email"
          label="Email"
          type="email"
          placeholder="Digite seu email"
        />
        <FormField
          id="password"
          label="Senha"
          type="password"
          placeholder="Digite sua senha"
        />
        <FormField
          id="confirmPassword"
          label="Confirmar senha"
          type="password"
          placeholder="Confirme sua senha"
        />
      </Flex>

      <Flex className={styles.fields} hidden={step !== 1}>
        <Flex className={styles.fieldsRow}>
          <FormField id="zipCode" label="CEP" placeholder="Digite seu CEP" />
          <FormField id="state" label="Estado" placeholder="UF" />
        </Flex>
        <FormField id="city" label="Cidade" placeholder="Digite sua cidade" />
        <FormField
          id="district"
          label="Bairro"
          placeholder="Digite seu bairro"
        />
        <FormField id="street" label="Rua" placeholder="Digite sua rua" />
        <Flex className={styles.fieldsRow}>
          <FormField
            id="complement"
            label="Complemento"
            placeholder="Complemento"
          />
          <FormField id="number" label="Número" placeholder="Número" />
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
          onClick={() => setStep(0)}
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
          onClick={() => setStep(1)}
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
            onClick={() => setStep(1)}
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
