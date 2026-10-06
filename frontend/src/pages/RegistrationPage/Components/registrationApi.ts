const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

/** Body expected by `POST /customers` (CustomerBodyDTO on the backend). */
export type CustomerRequest = {
  user: {
    name: string;
    email: string;
    password: string;
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

export type CustomerResponse = {
  id: string;
  userId: string;
};

/** Thrown when the backend answers with a non-2xx status. */
export class RegistrationApiError extends Error {
  readonly status: number;

  constructor(status: number) {
    super(`Cadastro respondeu com status ${status}`);
    this.status = status;
  }
}

/** User-facing description for a failed `createCustomer` call. */
export function registrationErrorMessage(error: unknown) {
  if (error instanceof RegistrationApiError) {
    return error.status >= 500
      ? "Erro no servidor. Tente novamente em instantes."
      : "Verifique os dados informados e tente novamente.";
  }
  return "Não foi possível conectar ao servidor. Tente novamente.";
}

/**
 * Creates a customer on the backend. Throws `RegistrationApiError` on HTTP
 * failures and a `TypeError` when the server can't be reached.
 */
export async function createCustomer(
  body: CustomerRequest,
): Promise<CustomerResponse> {
  const response = await fetch(`${API_URL}/customers`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  if (!response.ok) {
    throw new RegistrationApiError(response.status);
  }

  return (await response.json()) as CustomerResponse;
}
