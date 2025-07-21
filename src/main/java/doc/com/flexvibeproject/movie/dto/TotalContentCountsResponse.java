package doc.com.flexvibeproject.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalContentCountsResponse {
    private int filmCount;
    private List<SerialSummaryResponse> Serial; // Matches the exact key name "Serial"
    private int concertCount;
}