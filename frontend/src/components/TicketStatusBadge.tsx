import {
  ticketStatusLabels,
  type TicketStatus,
} from "../models/ticketStatus";
import "./TicketStatusBadge.css";

type TicketStatusBadgeProps = {
  status: TicketStatus;
};

function TicketStatusBadge({ status }: TicketStatusBadgeProps) {
  return (
    <span className={`ticket-status-badge ticket-status-badge--${status}`}>
      {ticketStatusLabels[status]}
    </span>
  );
}

export default TicketStatusBadge;
