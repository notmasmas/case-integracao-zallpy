import Logo from "../../assets/logo.svg";
import RegistrationForms from "./Components/RegistrationForms";
import styles from "./RegistrationPage.module.css";

function RegistrationPage() {
  return (
    <div className={styles.page}>
      <div className={styles.layout}>
        <section className={styles.brand}>
          <img className={styles.logo} src={Logo} alt="Logo EcoVolt 360" />
          <h1 className={styles.slogan}>
            Onde há Sol, <br /> há energia
          </h1>
        </section>

        <section className={styles.form}>
          <RegistrationForms />
        </section>
      </div>
    </div>
  );
}

export default RegistrationPage;
