import { useState } from "react";
import { Box, Button, Field, Input } from "@chakra-ui/react";
import styles from "./RegistrationForms.module.css";
import { FiUserPlus } from "react-icons/fi";

function RegistrationForms() {
  const [step, setStep] = useState(0);

  return (
    <Box className={styles.form}>
      <div className={styles.icon}>
        <FiUserPlus />
      </div>

      <Box className={styles.header}>
        <h2 className={styles.title}>Cadastro</h2>
      </Box>

      <Box className={styles.fields}>
        {step === 0 ? (
          <>
            <Field.Root>
              <Field.Label>Nome completo</Field.Label>
              <Input
                type="text"
                placeholder="Digite seu nome completo"
                className={styles.input}
              />
            </Field.Root>
            <Field.Root>
              <Field.Label>Email</Field.Label>
              <Input
                type="email"
                placeholder="Digite seu email"
                className={styles.input}
              />
            </Field.Root>
            <Field.Root>
              <Field.Label>Senha</Field.Label>
              <Input
                type="password"
                placeholder="Digite sua senha"
                className={styles.input}
              />
            </Field.Root>
            <Field.Root>
              <Field.Label>Confirmar senha</Field.Label>
              <Input
                type="password"
                placeholder="Confirme sua senha"
                className={styles.input}
              />
            </Field.Root>
          </>
        ) : (
          <>
            <Box className={styles.fieldsRow}>
              <Field.Root className={styles.field}>
                <Field.Label>CEP</Field.Label>
                <Input
                  type="text"
                  placeholder="Digite seu CEP"
                  className={styles.input}
                />
              </Field.Root>
              <Field.Root className={styles.field}>
                <Field.Label>Estado</Field.Label>
                <Input
                  type="text"
                  placeholder="UF"
                  className={styles.input}
                />
              </Field.Root>
            </Box>
            <Field.Root>
              <Field.Label>Cidade</Field.Label>
              <Input
                type="text"
                placeholder="Digite sua cidade"
                className={styles.input}
              />
            </Field.Root>
            <Field.Root>
              <Field.Label>Bairro</Field.Label>
              <Input
                type="text"
                placeholder="Digite seu bairro"
                className={styles.input}
              />
            </Field.Root>
            <Field.Root>
              <Field.Label>Rua</Field.Label>
              <Input
                type="text"
                placeholder="Digite sua rua"
                className={styles.input}
              />
            </Field.Root>
            <Box className={styles.fieldsRow}>
              <Field.Root className={styles.field}>
                <Field.Label>Complemento</Field.Label>
                <Input
                  type="text"
                  placeholder="Complemento"
                  className={styles.input}
                />
              </Field.Root>
              <Field.Root className={styles.field}>
                <Field.Label>Número</Field.Label>
                <Input
                  type="text"
                  placeholder="Número"
                  className={styles.input}
                />
              </Field.Root>
            </Box>
          </>
        )}
      </Box>

      <Box className={styles.dots}>
        <button
          type="button"
          aria-label="Ir para etapa 1"
          className={`${styles.dot}${step === 0 ? ` ${styles.dotActive}` : ""}`}
          onClick={() => setStep(0)}
        />
        <button
          type="button"
          aria-label="Ir para etapa 2"
          className={`${styles.dot}${step === 1 ? ` ${styles.dotActive}` : ""}`}
          onClick={() => setStep(1)}
        />
      </Box>

      <Box className={styles.actions}>
        <Button className={styles.submit}>Cadastrar</Button>
      </Box>
    </Box>
  );
}

export default RegistrationForms;
