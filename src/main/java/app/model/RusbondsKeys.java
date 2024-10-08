package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RusbondsKeys
{
     String spid;
     String spcs;
     String accessToken;
     String userAgent;
     String refreshToken;
}
