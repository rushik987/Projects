package ca.sheridancollege.bhavsvir.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Review {

	private Integer id;
    private String bookTitle;
    private String reviewText;
    private User user;
}
