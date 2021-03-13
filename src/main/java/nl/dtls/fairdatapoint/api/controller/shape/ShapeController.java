/**
 * The MIT License
 * Copyright © 2017 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.dtls.fairdatapoint.api.controller.shape;

import nl.dtls.fairdatapoint.api.dto.shape.ShapeChangeDTO;
import nl.dtls.fairdatapoint.api.dto.shape.ShapeDTO;
import nl.dtls.fairdatapoint.api.dto.shape.ShapeImportDTO;
import nl.dtls.fairdatapoint.api.dto.shape.ShapeRemoteDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.shape.ShapeService;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@RestController
@RequestMapping("/shapes")
public class ShapeController {

    @Autowired
    private ShapeService shapeService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ShapeDTO>> getShapes() {
        List<ShapeDTO> dto = shapeService.getShapes();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/public", method = RequestMethod.GET)
    public ResponseEntity<List<ShapeDTO>> getPublishedShapes() {
        List<ShapeDTO> dto = shapeService.getPublishedShapes();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/import", method = RequestMethod.GET)
    public ResponseEntity<List<ShapeRemoteDTO>> getImportableShapes(@RequestParam(name = "from") String fdpUrl) {
        List<ShapeRemoteDTO> dto = shapeService.getRemoteShapes(fdpUrl);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResponseEntity<List<ShapeDTO>> importShapes(@RequestBody @Valid ShapeImportDTO reqDto) {
        List<ShapeDTO> dto = shapeService.importShapes(reqDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ShapeDTO> createShape(@RequestBody @Valid ShapeChangeDTO reqDto) {
        ShapeDTO dto = shapeService.createShape(reqDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<ShapeDTO> getShape(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        Optional<ShapeDTO> oDto = shapeService.getShapeByUuid(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format("Shape '%s' doesn't exist", uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET, produces = {"!application/json"})
    public ResponseEntity<Model> getShapeContent(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        Optional<Model> oDto = shapeService.getShapeContentByUuid(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format("Shape '%s' doesn't exist", uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public ResponseEntity<ShapeDTO> putShape(@PathVariable final String uuid,
                                             @RequestBody @Valid ShapeChangeDTO reqDto) throws ResourceNotFoundException {
        Optional<ShapeDTO> oDto = shapeService.updateShape(uuid, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format("Shape '%s' doesn't exist", uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteShape(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        boolean result = shapeService.deleteShape(uuid);
        if (result) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResourceNotFoundException(format("Shape '%s' doesn't exist", uuid));
        }
    }

}
