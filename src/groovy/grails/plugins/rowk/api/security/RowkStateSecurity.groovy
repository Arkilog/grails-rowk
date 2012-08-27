package ma.akrilog.rowk.api.security

public interface RowkStateSecurity {

	boolean canView()

	boolean canTriggerEvent(String workflowEvent)
	
	boolean canReadVar(String workflowVariable)
	
	boolean canWriteVar(String workflowVariable)

	List<String> events()

	List<String> vars()

}