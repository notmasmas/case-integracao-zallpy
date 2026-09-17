import { Box, Button, Checkbox, Field, Input } from "@chakra-ui/react";
import "../loginPage.css";

function LoginPageForm() {
  return (
    <Box className="login-form">
      <Box className="login-form__header">
        <p className="login-form__title">Login</p>
      </Box>

      <Box className="login-form__fields">
        <Field.Root>
          <Field.Label>Email</Field.Label>
          <Input
            type="email"
            placeholder="Digite seu email"
            className="login-form__input"
          />
        </Field.Root>
        <Field.Root>
          <Field.Label>Senha</Field.Label>
          <Input
            type="password"
            placeholder="Digite sua senha"
            className="login-form__input"
          />
        </Field.Root>
      </Box>
      <Box className="login-form__options">
        <Box className="login-form__options-row">
          <Box>
            <Checkbox.Root>
              <Checkbox.HiddenInput />
              <Checkbox.Control className="login-form__checkbox">
                <Checkbox.Indicator />
              </Checkbox.Control>
              <Checkbox.Label className="login-form__checkbox-label">
                Lembrar senha
              </Checkbox.Label>
            </Checkbox.Root>
          </Box>
          <Box>
            <a className="login-form__link" href="#">
              Esqueceu sua senha?
            </a>
          </Box>
        </Box>
      </Box>
      <Box className="login-form__actions">
        <Box>
          <Button className="login-form__submit">Entrar</Button>
        </Box>
      </Box>
      <Box className="login-form__footer">
        <a className="login-form__link" href="#">
          Primeiro acesso?
        </a>
        <a className="login-form__link" href="#">
          Perguntas frequentes
        </a>
      </Box>
    </Box>
  );
}

export default LoginPageForm;
