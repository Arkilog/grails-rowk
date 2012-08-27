package ma.akrilog.rowk.api.security

public interface RowkSecurity {

	RowkParticipantSecurity currentUser(session)

	RowkParticipantSecurity user(String username)

	List<RowkParticipantSecurity> group(String groupName)

	List<RowkParticipantSecurity> role(String roleName)

	List<String> users()

	List<String> roles()
	
	List<String> groups()

}