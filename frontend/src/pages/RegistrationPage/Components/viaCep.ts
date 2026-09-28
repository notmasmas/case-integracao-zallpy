export type ViaCepAddress = {
  state: string;
  city: string;
  district: string;
  street: string;
};

type ViaCepResponse = {
  uf?: string;
  localidade?: string;
  bairro?: string;
  logradouro?: string;
  erro?: boolean | string;
};

/**
 * Looks up an address on ViaCEP. Resolves to `null` when the CEP does not
 * exist and throws on network/HTTP failures (including aborts).
 */
export async function fetchAddressByZipCode(
  zipCodeDigits: string,
  signal?: AbortSignal,
): Promise<ViaCepAddress | null> {
  const response = await fetch(
    `https://viacep.com.br/ws/${zipCodeDigits}/json/`,
    { signal },
  );

  if (!response.ok) {
    throw new Error(`ViaCEP respondeu com status ${response.status}`);
  }

  const data = (await response.json()) as ViaCepResponse;
  if (data.erro) return null;

  return {
    state: data.uf ?? "",
    city: data.localidade ?? "",
    district: data.bairro ?? "",
    street: data.logradouro ?? "",
  };
}
