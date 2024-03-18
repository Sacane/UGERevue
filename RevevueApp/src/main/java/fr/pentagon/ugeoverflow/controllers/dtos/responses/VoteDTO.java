package fr.pentagon.ugeoverflow.controllers.dtos.responses;

public record VoteDTO(long upVotes, long downVotes) {

    public VoteDTO {
        if(upVotes < 0 || downVotes < 0)
            throw new IllegalArgumentException("Cannot be negative.");
    }

}
