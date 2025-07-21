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
public class ContentCountsResponse {
    private int filmCount;
    private List<SerialCountResponse> serialCounts;
    private int concertCount;
}
