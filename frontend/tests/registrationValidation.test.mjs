import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import { test } from "node:test";
import ts from "typescript";

const source = await readFile(
  new URL("../src/pages/RegistrationPage/Components/registrationValidation.ts", import.meta.url),
  "utf8",
);
const { outputText } = ts.transpileModule(source, {
  compilerOptions: { module: ts.ModuleKind.ESNext, target: ts.ScriptTarget.ES2022 },
});
const {
  formatFiscalId,
  initialRegistrationValues,
  validateField,
  validationMessages,
} = await import(`data:text/javascript;base64,${Buffer.from(outputText).toString("base64")}`);

test("formats a CPF while typing and switches to CNPJ after the eleventh digit", () => {
  let value = "";
  for (const digit of "12345678901") value = formatFiscalId(value + digit);
  assert.equal(value, "123.456.789-01");

  for (const digit of "234") value = formatFiscalId(value + digit);
  assert.equal(value, "12.345.678/9012-34");
});

test("returns to the CPF mask when deleting from a CNPJ", () => {
  let value = formatFiscalId("12345678901234");
  for (let i = 0; i < 3; i++) value = formatFiscalId(value.slice(0, -1));
  assert.equal(value, "123.456.789-01");
});

test("rejects 12 or 13 digits while accepting 11 or 14", () => {
  for (const length of [11, 12, 13, 14]) {
    const values = { ...initialRegistrationValues, fiscalId: "1".repeat(length) };
    assert.equal(
      validateField("fiscalId", values),
      length === 11 || length === 14 ? undefined : validationMessages.fiscalId,
    );
  }
});
