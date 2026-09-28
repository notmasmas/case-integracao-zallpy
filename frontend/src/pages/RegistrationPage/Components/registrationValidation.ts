export type RegistrationFormValues = {
  fullName: string;
  cpf: string;
  email: string;
  password: string;
  confirmPassword: string;
  zipCode: string;
  state: string;
  city: string;
  district: string;
  street: string;
  complement: string;
  number: string;
};

export type RegistrationField = keyof RegistrationFormValues;

export type FormErrors = Partial<Record<RegistrationField, string>>;

export const PASSWORD_MIN_LENGTH = 8;
export const PASSWORD_MAX_LENGTH = 25;

export const validationMessages = {
  required: "Campo obrigatório.",
  email: "Informe um email válido.",
  cpf: "Informe um CPF com 11 dígitos.",
  cpfNotFound: "CPF não encontrado na base de clientes.",
  passwordLength: `A senha deve ter entre ${PASSWORD_MIN_LENGTH} e ${PASSWORD_MAX_LENGTH} caracteres.`,
  passwordPattern: "A senha deve conter letras e números.",
  passwordHint: `Use de ${PASSWORD_MIN_LENGTH} a ${PASSWORD_MAX_LENGTH} caracteres, com letras e números.`,
  passwordMismatch: "As senhas não coincidem.",
  zipCode: "Informe um CEP válido.",
  zipCodeNotFound: "CEP não encontrado.",
  zipCodeUnavailable:
    "Não foi possível buscar o CEP. Preencha o endereço manualmente.",
  state: "Informe a UF com 2 letras.",
} as const;

export const initialRegistrationValues: RegistrationFormValues = {
  fullName: "",
  cpf: "",
  email: "",
  password: "",
  confirmPassword: "",
  zipCode: "",
  state: "",
  city: "",
  district: "",
  street: "",
  complement: "",
  number: "",
};

export const stepFields: RegistrationField[][] = [
  ["fullName", "cpf", "email", "password", "confirmPassword"],
  ["zipCode", "state", "city", "district", "street", "complement", "number"],
];

const optionalFields: RegistrationField[] = ["complement"];

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const LETTER_PATTERN = /\p{L}/u;
const DIGIT_PATTERN = /\d/;
const STATE_PATTERN = /^[A-Z]{2}$/;

export function onlyDigits(value: string) {
  return value.replace(/\D/g, "");
}

export function formatZipCode(value: string) {
  const digits = onlyDigits(value).slice(0, 8);
  return digits.length > 5
    ? `${digits.slice(0, 5)}-${digits.slice(5)}`
    : digits;
}

export function formatCpf(value: string) {
  const digits = onlyDigits(value).slice(0, 11);
  if (digits.length > 9) {
    return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9)}`;
  }
  if (digits.length > 6) {
    return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6)}`;
  }
  if (digits.length > 3) {
    return `${digits.slice(0, 3)}.${digits.slice(3)}`;
  }
  return digits;
}

export function validatePassword(value: string) {
  if (!value) return validationMessages.required;
  if (
    value.length < PASSWORD_MIN_LENGTH ||
    value.length > PASSWORD_MAX_LENGTH
  ) {
    return validationMessages.passwordLength;
  }
  if (!LETTER_PATTERN.test(value) || !DIGIT_PATTERN.test(value)) {
    return validationMessages.passwordPattern;
  }
  return undefined;
}

export function validateConfirmPassword(value: string, password: string) {
  if (!value) return validationMessages.required;
  if (value !== password) return validationMessages.passwordMismatch;
  return undefined;
}

export function validateField(
  field: RegistrationField,
  values: RegistrationFormValues,
): string | undefined {
  const value = values[field].trim();

  switch (field) {
    case "password":
      return validatePassword(values.password);
    case "confirmPassword":
      return validateConfirmPassword(values.confirmPassword, values.password);
    case "email":
      if (!value) return validationMessages.required;
      return EMAIL_PATTERN.test(value) ? undefined : validationMessages.email;
    case "cpf":
      if (!value) return validationMessages.required;
      return onlyDigits(value).length === 11 ? undefined : validationMessages.cpf;
    case "zipCode":
      if (!value) return validationMessages.required;
      return onlyDigits(value).length === 8
        ? undefined
        : validationMessages.zipCode;
    case "state":
      if (!value) return validationMessages.required;
      return STATE_PATTERN.test(value) ? undefined : validationMessages.state;
    default:
      if (optionalFields.includes(field)) return undefined;
      return value ? undefined : validationMessages.required;
  }
}

/** Returns an entry for every field of the step (undefined when valid). */
export function validateStep(values: RegistrationFormValues, step: number) {
  const errors: FormErrors = {};
  for (const field of stepFields[step]) {
    errors[field] = validateField(field, values);
  }
  return errors;
}

export function hasErrors(errors: FormErrors) {
  return Object.values(errors).some(Boolean);
}
