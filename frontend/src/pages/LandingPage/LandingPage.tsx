import { useEffect, useState } from "react";
import { Box, Button, Flex, Heading, Image, Text } from "@chakra-ui/react";
import { Link as RouterLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { homePath, paths } from "../../routes/routes";
import LogoEcoVolt from "../../assets/landing/logo-ecovolt.svg";
import Divider from "../../assets/landing/divider.svg";
import IconeValorizacao from "../../assets/landing/icone-valorizacao.png";
import IconeEconomia from "../../assets/landing/icone-economia.png";
import IconeSuporte from "../../assets/landing/icone-suporte.png";
import IconeEnergiaLimpa from "../../assets/landing/icone-energia-limpa.png";
import styles from "./LandingPage.module.css";

// Seções da landing. Só "inicio" existe hoje; as demais ganham o id
// correspondente quando forem criadas.
const sectionIds = ["inicio", "solucoes", "sobre", "contato"] as const;

type SectionId = (typeof sectionIds)[number];

// Itens do menu: a maioria rola até uma seção da landing; "FAQ" navega
// para a página /faq e por isso não entra na lógica de seção ativa.
type NavItem =
  | { label: string; sectionId: SectionId }
  | { label: string; path: string };

const navItems: readonly NavItem[] = [
  { label: "Início", sectionId: "inicio" },
  { label: "FAQ", path: paths.faq },
  { label: "Soluções", sectionId: "solucoes" },
  { label: "Sobre", sectionId: "sobre" },
  { label: "Contato", sectionId: "contato" },
];

// Distância do topo a partir da qual uma seção passa a ser a "atual".
const activeSectionOffset = 120;

function currentSection(): SectionId {
  let active: SectionId = "inicio";
  for (const sectionId of sectionIds) {
    const section = document.getElementById(sectionId);
    if (section && section.getBoundingClientRect().top <= activeSectionOffset) {
      active = sectionId;
    }
  }
  return active;
}

// Ícones na mesma posição em que aparecem no Figma.
const benefits = [
  { icon: IconeValorizacao, lines: ["Economia", "na sua conta", "de energia"] },
  { icon: IconeEconomia, lines: ["Valorização", "do seu imóvel"] },
  { icon: IconeSuporte, lines: ["Suporte completo", "do projeto a instalação"] },
  { icon: IconeEnergiaLimpa, lines: ["Energia limpa", "e renovável"] },
];

function LandingPage() {
  const { session } = useAuth();
  const enterPath = session ? homePath(session.role) : paths.login;
  const [activeSection, setActiveSection] = useState<SectionId>("inicio");
  const navigate = useNavigate();

  useEffect(() => {
    const handleScroll = () => setActiveSection(currentSection());
    window.addEventListener("scroll", handleScroll, { passive: true });
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  function goToSection(sectionId: SectionId) {
    setActiveSection(sectionId);
    if (sectionId === "inicio") {
      window.scrollTo({ top: 0, behavior: "smooth" });
      return;
    }
    document.getElementById(sectionId)?.scrollIntoView({ behavior: "smooth" });
  }

  return (
    <Flex className={styles.page} direction="column">
      <Flex as="header" className={styles.header}>
        <Flex className={styles.brand} align="center" gap="2.5">
          <Flex boxSize="11" align="center" justify="center" flexShrink={0}>
            <Image src={LogoEcoVolt} alt="" />
          </Flex>
          <Flex direction="column">
            <Text as="span" className={styles.brandName}>
              Eco Volt360
            </Text>
            <Text as="span" className={styles.brandTag}>
              Portal
            </Text>
          </Flex>
        </Flex>

        <Flex className={styles.actions}>
          <Flex as="nav" className={styles.nav} aria-label="Navegação principal">
            {navItems.map((item) => {
              const isSection = "sectionId" in item;
              return (
                <Button
                  key={item.label}
                  type="button"
                  variant="plain"
                  size="sm"
                  px="0"
                  className={styles.navLink}
                  aria-current={
                    isSection && activeSection === item.sectionId
                      ? "location"
                      : undefined
                  }
                  onClick={() =>
                    isSection ? goToSection(item.sectionId) : navigate(item.path)
                  }
                >
                  {item.label}
                </Button>
              );
            })}
          </Flex>

          <Button
            asChild
            className={styles.enterButton}
            height="9"
            minW="9.1875rem"
            px="4"
            rounded="lg"
          >
            <RouterLink to={enterPath}>Entrar</RouterLink>
          </Button>
        </Flex>
      </Flex>

      <Flex as="main" id="inicio" className={styles.hero} direction="column" gap="4">
        <Flex align="center" gap="2">
          <Box className={styles.eyebrowBar} width="8" height="3px" rounded="sm" />
          <Text as="span" className={styles.eyebrow}>
            Portal do Cliente
          </Text>
          <Box className={styles.eyebrowBar} width="8" height="3px" rounded="sm" />
        </Flex>

        <Heading as="h1" className={styles.title}>
          Onde há Sol,
          <br />
          há energia.
        </Heading>

        <Text className={styles.subtitle}>
          Acompanhe seu projeto solar, monitore sua geração e economize mais com
          o Eco Volt360.
        </Text>
      </Flex>

      <Box as="ul" className={styles.benefits}>
        {benefits.map((benefit, index) => (
          <Flex as="li" key={benefit.lines[0]} className={styles.benefit}>
            {index > 0 && (
              <Image src={Divider} alt="" className={styles.divider} />
            )}
            <Image src={benefit.icon} alt="" className={styles.benefitIcon} />
            <Text className={styles.benefitText}>
              {benefit.lines.map((line, lineIndex) => (
                <span key={line}>
                  {lineIndex > 0 && <br />}
                  {line}
                </span>
              ))}
            </Text>
          </Flex>
        ))}
      </Box>
    </Flex>
  );
}

export default LandingPage;
