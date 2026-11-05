import { projectStatusLabel } from "../api/projects";
import "./ProjectStatusBadge.css";

type ProjectStatusBadgeProps = {
  status: string | null;
};

function ProjectStatusBadge({ status }: ProjectStatusBadgeProps) {
  var modifier;

  switch(status) {
    case('AWAITING_INSTALLATION'):
      modifier = '--awaiting-installation'
      break;
    case('AWAITING_APPROVAL'):
      modifier = '--awaiting-approval'
      break;
    case('INSTALLATION_SCHEDULE'):
      modifier='--installation-schedule'
      break;
    case('INSTALLATION_IN_PROGRESS'):
      modifier='--installation-in-progress'
      break;
    case('IN_PROGRESS'):
      modifier='--in-progress'
      break;
    case('INSTALLED'):
      modifier='--installed';
      break;
    case('COMPLETED'):
      modifier='--completed';
      break;
    case('CANCELLED'):
      modifier='--cancelled';
      break;
    default:
      modifier='';
  }

  return (
    <span className={`project-status-badge${modifier} project-status-badge`}>
      {projectStatusLabel(status)}
    </span>
  );
}

export default ProjectStatusBadge;