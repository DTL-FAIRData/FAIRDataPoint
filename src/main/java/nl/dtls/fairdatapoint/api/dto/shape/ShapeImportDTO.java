package nl.dtls.fairdatapoint.api.dto.shape;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShapeImportDTO {
    List<ShapeRemoteDTO> shapes;
}
