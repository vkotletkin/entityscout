package ru.kotletkin.entityscout.search;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.kotletkin.entityscout.common.dto.BaseRequest;
import ru.kotletkin.entityscout.search.dto.SearchSingleDTO;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/single")
    public SearchSingleDTO searchSingle(@Valid @RequestBody BaseRequest request,
                                        @RequestParam("query") @NotBlank String query) {
        return searchService.searchBySingleRequest(request.text(), query);
    }
}
