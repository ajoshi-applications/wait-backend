package org.arturjoshi.users.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Identifiable;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Page<T extends Identifiable<Long>> {
    private List<T> content;
    private boolean first;
    private boolean last;
    private Integer size;
    private Integer totalPages;
    private Integer totalElements;
    private Integer numberOfElements;
    private Integer number;
}
