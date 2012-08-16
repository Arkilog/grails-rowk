package ma.akrilog.rowk.api.security

public interface RowkParticipantSecurity {

	String name()

	String email()

	RowkWorkflowSecurity workflow(String workflowName)

	List<String> workflows()

}