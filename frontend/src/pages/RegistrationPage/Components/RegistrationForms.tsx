import { useState } from "react";
import { Box, Button, Field, Input } from "@chakra-ui/react";
import "./RegistrationForms.css";
import { FiUserPlus } from "react-icons/fi";

function RegistrationForms() {
  const [step, setStep] = useState(0);

  return (
    <Box className="registration-form">
        
      <div className="icon"><FiUserPlus/></div>
    
      <Box className="registration-form__header">
        <p className="registration-form__title">Cadastro</p>
      </Box>

      <Box className="registration-form__fields">
        {step === 0 ? (
          <>
            <Field.Root>
              <Field.Label>Nome completo</Field.Label>
              <Input
                type="text"
                placeholder="Digite seu nome completo"
                className="registration-form__input"
              />
            </Field.Root>
            <Field.Root>
              <Field.Label>Email</Field.Label>
              <Input
                type="email"
                placeholder="Digite seu email"
                className="registration-form__input"
              />
            </Field.Root>
            <Field.Root>
              <Field.Label>Senha</Field.Label>
              <Input
                type="password"
                placeholder="Digite sua senha"
                className="registration-form__input"
              />
            </Field.Root>
            <Field.Root>
              <Field.Label>Confirmar senha</Field.Label>
              <Input
                type="password"
                placeholder="Confirme sua senha"
                className="registration-form__input"
              />
            </Field.Root>
          </>
        ) : (
          <>
            <Box className="registration-form__fields-row">
              <Field.Root className="registration-form__field">
                <Field.Label>CEP</Field.Label>
                <Input
                  type="text"
                  placeholder="Digite seu CEP"
                  className="registration-form__input"
                />
              </Field.Root>
              <Field.Root className="registration-form__field">
                <Field.Label>Estado</Field.Label>
                <Input
                  type="text"
                  placeholder="UF"
                  className="registration-form__input"
                />
              </Field.Root>
            </Box>
            <Field.Root>
              <Field.Label>Cidade</Field.Label>
              <Input
                type="text"
                placeholder="Digite sua cidade"
                className="registration-form__input"
              />
            </Field.Root>
            <Field.Root>
              <Field.Label>Bairro</Field.Label>
              <Input
                type="text"
                placeholder="Digite seu bairro"
                className="registration-form__input"
              />
            </Field.Root>
            <Field.Root>
              <Field.Label>Rua</Field.Label>
              <Input
                type="text"
                placeholder="Digite sua rua"
                className="registration-form__input"
              />
            </Field.Root>
            <Box className="registration-form__fields-row">
              <Field.Root className="registration-form__field">
                <Field.Label>Complemento</Field.Label>
                <Input
                  type="text"
                  placeholder="Complemento"
                  className="registration-form__input"
                />
              </Field.Root>
              <Field.Root className="registration-form__field">
                <Field.Label>Número</Field.Label>
                <Input
                  type="text"
                  placeholder="Número"
                  className="registration-form__input"
                />
              </Field.Root>
            </Box>
          </>
        )}
      </Box>

      <Box className="registration-form__dots">
        <button
          type="button"
          aria-label="Ir para etapa 1"
          className={`registration-form__dot${
            step === 0 ? " registration-form__dot--active" : ""
          }`}
          onClick={() => setStep(0)}
        />
        <button
          type="button"
          aria-label="Ir para etapa 2"
          className={`registration-form__dot${
            step === 1 ? " registration-form__dot--active" : ""
          }`}
          onClick={() => setStep(1)}
        />
      </Box>

      <Box className="registration-form__actions">
        <Button className="registration-form__submit">Cadastrar</Button>
      </Box>
    </Box>
  );
}

export default RegistrationForms;
