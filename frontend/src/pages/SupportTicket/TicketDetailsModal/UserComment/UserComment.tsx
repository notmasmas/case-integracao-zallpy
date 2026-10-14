import { Box } from "@chakra-ui/react";
import "./UserComment.css";
import type { TicketMessage } from "../../../../api/tickets";

const roleLabels: Record<string, string> = {
    CUSTOMER: "Cliente",
    SUPPORT: "Suporte",
};

export default function UserComment({ message }: { message: TicketMessage }) {
    const createdAt = new Date(message.createdAt);
    const formattedDate = Number.isNaN(createdAt.getTime())
        ? message.createdAt
        : new Intl.DateTimeFormat("pt-BR", {
                dateStyle: "short",
                timeStyle: "short",
            }).format(createdAt);

    return (
        <Box className="user__comment__body">
            <Box className="user__comment__body__header">
                <span className="user__comment__name">
                    {roleLabels[message.role] ?? message.role}
                </span>
                <span>{formattedDate}</span>
            </Box>
            <Box>
                <p>{message.content}</p>
            </Box>
        </Box>
    );
}