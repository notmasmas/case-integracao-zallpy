import { useState } from "react";
import { FiUserPlus } from "react-icons/fi";
import styles from "./RegistrationForms.module.css";

type FormFieldProps = {
  id: string;
  label: string;
  type?: "text" | "email" | "password";
  placeholder: string;
};

function FormField({ id, label, type = "text", placeholder }: FormFieldProps) {
  return (
    <div className={styles.field}>
      <label className={styles.label} htmlFor={id}>
        {label}
      </label>
      <input
        id={id}
        name={id}
        type={type}
        placeholder={placeholder}
        className={styles.input}
      />
    </div>
  );
}

function RegistrationForms() {
  const [step, setStep] = useState(0);

  return (
    <section className={styles.form} aria-labelledby="registration-title">
      <div className={styles.icon} aria-hidden="true">
        <FiUserPlus />
      </div>

      <header className={styles.header}>
        <h2 id="registration-title" className={styles.title}>
          Cadastro
        </h2>
      </header>

      <div className={styles.fields}>
        {step === 0 ? (
          <>
            <FormField
              id="fullName"
              label="Nome completo"
              placeholder="Digite seu nome completo"
            />
            <FormField
              id="email"
              label="Email"
              type="email"
              placeholder="Digite seu email"
            />
            <FormField
              id="password"
              label="Senha"
              type="password"
              placeholder="Digite sua senha"
            />
            <FormField
              id="confirmPassword"
              label="Confirmar senha"
              type="password"
              placeholder="Confirme sua senha"
            />
          </>
        ) : (
          <>
            <div className={styles.fieldsRow}>
              <FormField id="zipCode" label="CEP" placeholder="Digite seu CEP" />
              <FormField id="state" label="Estado" placeholder="UF" />
            </div>
            <FormField
              id="city"
              label="Cidade"
              placeholder="Digite sua cidade"
            />
            <FormField
              id="district"
              label="Bairro"
              placeholder="Digite seu bairro"
            />
            <FormField id="street" label="Rua" placeholder="Digite sua rua" />
            <div className={styles.fieldsRow}>
              <FormField
                id="complement"
                label="Complemento"
                placeholder="Complemento"
              />
              <FormField id="number" label="Número" placeholder="Número" />
            </div>
          </>
        )}
      </div>

      <div className={styles.dots}>
        <button
          type="button"
          aria-label="Ir para etapa 1"
          aria-current={step === 0 ? "step" : undefined}
          className={`${styles.dot}${step === 0 ? ` ${styles.dotActive}` : ""}`}
          onClick={() => setStep(0)}
        />
        <button
          type="button"
          aria-label="Ir para etapa 2"
          aria-current={step === 1 ? "step" : undefined}
          className={`${styles.dot}${step === 1 ? ` ${styles.dotActive}` : ""}`}
          onClick={() => setStep(1)}
        />
      </div>

      <div className={styles.actions}>
        <button type="button" className={styles.submit}>
          Cadastrar
        </button>
      </div>
    </section>
  );
}

export default RegistrationForms;
