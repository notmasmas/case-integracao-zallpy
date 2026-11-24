import { Box } from "@chakra-ui/react";
import { LuStar } from "react-icons/lu";


function WithoutScorePage() {
    return(
        <Box padding="1rem" display="flex" width="100%" flexDirection="column" alignItems="center" justifyContent="center" gap={2} textAlign="center">
            <span><LuStar color="var(--color-primary)" size={60} /></span>
            <h2 style={{ fontSize: "var(--font-size-heading-lg)", fontWeight: "var(--font-weight-display-semibold" }}>Nenhuma avaliação ainda</h2>
            <p style={{ fontSize: "var(--font-size-body-lead)" , 
                alignItems: "center",
                 display: "flex", 
                 flexDirection: "column",
                  gap: 2, maxWidth:"30rem", 
                  color:"var(--color-text-secondary)"}}>
                    As notas aparecem aqui quando os clientes avaliarem chamados que você resolveu.Enquanto isso, acompanhe sua fila no painel principal.</p>
        </Box>
    )
}

export default WithoutScorePage