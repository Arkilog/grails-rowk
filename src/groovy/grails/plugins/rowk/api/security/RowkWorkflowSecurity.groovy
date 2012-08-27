package ma.akrilog.rowk.api.security

public interface RowkWorkflowSecurity {

	RowkStateSecurity state(String stateName)

 	List<String> states()
	
}