package app.integration.dto.output;

public record HintDto (String query, int page, int size, int[] types){
}
