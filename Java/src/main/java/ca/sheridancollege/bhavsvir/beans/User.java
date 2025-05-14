package ca.sheridancollege.bhavsvir.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class User {

	private Long userId;
	private String email;
	private String encryptedPassword;
	private Boolean enabled;
}
