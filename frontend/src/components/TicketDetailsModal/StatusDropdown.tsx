import { NativeSelect } from "@chakra-ui/react";
import type { ChangeEvent } from "react";
import {
  ticketStatusLabels,
  type TicketStatus,
} from "../../models/ticketStatus";

type StatusDropdownProps = {
  status: TicketStatus;
  onChange: (status: TicketStatus) => void;
};

const statuses = Object.keys(ticketStatusLabels) as TicketStatus[];

export default function StatusDropdown({ status, onChange }: StatusDropdownProps) {
  const handleChange = (event: ChangeEvent<HTMLSelectElement>) => {
    onChange(event.target.value as TicketStatus);
  };

  return (
    <NativeSelect.Root className="ticket-status-dropdown">
      <NativeSelect.Field
        aria-label="Status do chamado"
        className={`ticket-status-badge ticket-status-badge--${status} ticket-status-dropdown__field`}
        value={status}
        onChange={handleChange}
      >
        {statuses.map((ticketStatus) => (
          <option key={ticketStatus} value={ticketStatus}>
            {ticketStatusLabels[ticketStatus]}
          </option>
        ))}
      </NativeSelect.Field>
      <NativeSelect.Indicator />
    </NativeSelect.Root>
  );
}