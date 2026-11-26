import { Box } from "@chakra-ui/react";
import { LuStar } from "react-icons/lu";


function WithoutScorePage() {
    return(
        <Box className="">
            <span><LuStar/></span>
            <h2>Nenhuma avaliação ainda</h2>
            <p>As notas aparecem aqui quando os clientes avaliarem chamados que você resolveu.<br/> Equanto isso, acompanhe sua fila no painel principal</p>
        </Box>
    )
}

export default WithoutScorePage