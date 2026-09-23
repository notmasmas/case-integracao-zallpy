import { Box, Button, Checkbox, Field, Input } from "@chakra-ui/react";
import "../loginPage.css";
import { useState } from "react";
import { toaster } from "../../../components/ui/toaster";

type LoginPageFormProps = {
  email: string;
  password: string;
};
function LoginPageForm() {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");

  const handleSubmit = (formData: LoginPageFormProps): void => {
    if (!formData.email.trim() || !formData.password.trim()) {
      toaster.create({
        title: "Login inválido",
        description: "Por favor, preencha todos os campos obrigatórios.",
        type: "error",
      });
    }
  };

  return (
    <Box className="login-form">
      <Box className="login-form__header">
        <p className="login-form__title">Login</p>
      </Box>

      <Box className="login-form__fields">
        <form
          className="login-form__fields"
          onSubmit={(e) => {
            e.preventDefault();
            handleSubmit({ email, password });
          }}
          id="login-form"
        >
          <Field.Root>
            <Field.Label className="login-form__label">Email</Field.Label>
            <Input
              type="email"
              placeholder="Digite seu email"
              className="login-form__input"
              onChange={(e) => setEmail(e.target.value)}
            />
          </Field.Root>
          <Field.Root>
            <Field.Label className="login-form__label">Senha</Field.Label>
            <Input
              type="password"
              placeholder="Digite sua senha"
              className="login-form__input"
              onChange={(e) => setPassword(e.target.value)}
            />
          </Field.Root>
        </form>
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
          <Button
            form="login-form"
            type="submit"
            className="login-form__submit"
          >
            Entrar
          </Button>
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
