import { Box, Button, Checkbox, Field, Input } from "@chakra-ui/react";

function LoginPageForm() {
  return (
    <Box
      display="flex"
      alignItems="center"
      width="min(500px, calc(100% - 20px))"
      height="620px"
      border="1px solid var(--color-border-black)"
      padding="2.5rem"
      gap="0.75rem"
      borderRadius="var(--radius-lg)"
      justifyContent="center"
      marginTop="2.5rem"
      flexDirection="column"
    >
      <Box display="flex" width="100%">
        <p
          style={{
            fontSize: "2rem",
            fontFamily: "var(--font-family)",
            fontWeight: "bold",
          }}
        >
          Login
        </p>
      </Box>

      <Box display="flex" width="100%" flexDirection="column" gap="1rem">
        <Field.Root>
          <Field.Label>Email</Field.Label>
          <Input
            type="email"
            placeholder="Digite seu email"
            padding="0.75rem"
            border="1px solid var(--color-border-gray)"
            borderRadius="var(--radius-lg)"
            backgroundColor="var( --color-surface-white)"
          />
        </Field.Root>
        <Field.Root>
          <Field.Label>Senha</Field.Label>
          <Input
            type="password"
            placeholder="Digite sua senha"
            padding="0.75rem"
            border="1px solid var(--color-border-gray)"
            borderRadius="var(--radius-lg)"
            backgroundColor="var( --color-surface-white)"
          />
        </Field.Root>
      </Box>
      <Box
        display="flex"
        width="100%"
        justifyContent="flex-start"
        marginTop="2rem"
      >
        <Box
          display="flex"
          width="100%"
          justifyContent="space-between"
          flexDirection="row"
        >
          <Box gap="0.5rem">
            <Checkbox.Root>
              <Checkbox.HiddenInput />
              <Checkbox.Control
                background="var(--color-surface-white)"
                border="1px solid var(--color-border-gray)"
                borderRadius="var(--radius-sm)"
              >
                <Checkbox.Indicator />
              </Checkbox.Control>
              <Checkbox.Label>Lembrar senha</Checkbox.Label>
            </Checkbox.Root>
          </Box>
          <Box>
            <a
              href="#"
              style={{
                color: "var(--color-gray)",
                textDecoration: "none",
              }}
            >
              Esqueceu sua senha?
            </a>
          </Box>
        </Box>
      </Box>
      <Box
        display="flex"
        width="100%"
        flexDirection="column"
        gap="1rem"
        marginTop="2rem"
      >
        <Box>
          <Button
            padding="1.5rem"
            borderRadius="var(--radius-lg)"
            backgroundColor="var(--color-primary)"
            color="var(--color-surface-white)"
            fontSize="1.25rem"
            width="100%"
          >
            Entrar
          </Button>
        </Box>
      </Box>
      <Box
        display="flex"
        width="100%"
        justifyContent="center"
        alignItems="center"
        marginTop="1rem"
        flexDirection="column"
        gap="1rem"
      >
        <a
          href="#"
          style={{
            color: "var(--color-gray)",
            textDecoration: "none",
          }}
        >
          Primeiro acesso?
        </a>
        <a
          href="#"
          style={{
            color: "var(--color-gray)",
            textDecoration: "none",
          }}
        >
          Perguntas frequentes
        </a>
      </Box>
    </Box>
  );
}

export default LoginPageForm;