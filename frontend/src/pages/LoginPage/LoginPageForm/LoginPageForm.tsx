import { Box, Button, Checkbox, Field, Input } from "@chakra-ui/react";
import axios from "axios";
import api from "../../../api/api";
import "../loginPage.css";
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toaster } from "../../../components/ui/toaster";
import { useAuth } from "../../../context/AuthContext";
import {
  expiredSessionSearch,
  homePath,
  paths,
  type UserRole,
} from "../../../routes/routes";

// Texto oficial do CA8 (falha no serviço de autenticação). Não sugere erro de credencial.
const LOGIN_FAILED_MESSAGE = "Não foi possível realizar o login";

type LoginPageFormProps = {
  email: string;
  password: string;
};

function LoginPageForm() {
  const navigate = useNavigate();
  const { setSession } = useAuth();

  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    if (params.get(expiredSessionSearch.key) !== expiredSessionSearch.value) {
      return;
    }
    params.delete(expiredSessionSearch.key);
    const query = params.toString();
    window.history.replaceState(null, "", query ? `${paths.login}?${query}` : paths.login);
    toaster.create({
      title: "Sessão inválida ou expirada",
      description: "Faça login novamente.",
      type: "error",
    });
  }, []);

  const handleSubmit = async (formData: LoginPageFormProps): Promise<void> => {
    if (isSubmitting) {
      return;
    }
    if (!formData.email.trim() || !formData.password.trim()) {
      toaster.create({
        title: "Login inválido",
        description: "Por favor, preencha todos os campos obrigatórios.",
        type: "error",
      });
      return;
    }
    setIsSubmitting(true);
    try {
      const response = await api.post<{
        role: UserRole;
        name: string;
        accessToken: string;
      }>("/login", formData);
      setSession({
        role: response.data.role,
        name: response.data.name,
        accessToken: response.data.accessToken,
      });
      navigate(homePath(response.data.role));
    } catch (error) {
      const status = axios.isAxiosError(error) ? error.response?.status : undefined;
      // 400 e 401: a API respondeu e recusou as credenciais.
      const credentialsRejected = status === 400 || status === 401;

      if (credentialsRejected) {
        toaster.create({
          title: "Login inválido",
          description: "E-mail ou senha inválidos.",
          type: "error",
        });
        return;
      }

      // 5xx, sem resposta (back-end fora) ou qualquer outra falha: não é erro de credencial.
      toaster.create({
        title: "Erro ao entrar",
        description: LOGIN_FAILED_MESSAGE,
        type: "error",
      });
    } finally {
      setIsSubmitting(false);
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
            loading={isSubmitting}
            disabled={isSubmitting}
          >
            Entrar
          </Button>
        </Box>
      </Box>
      <Box className="login-form__footer">
        <Link className="login-form__link" to={paths.registration}>
          Primeiro acesso?
        </Link>
        <a className="login-form__link" href="#">
          Perguntas frequentes
        </a>
      </Box>
    </Box>
  );
}

export default LoginPageForm;
