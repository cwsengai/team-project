package usecase.news;

import java.util.List;

public record NewsOutputData(String symbol, List<String> statements) {
}
