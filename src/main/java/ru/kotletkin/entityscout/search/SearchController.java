package ru.kotletkin.entityscout.search;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kotletkin.entityscout.search.dto.SearchSingleDTO;
import ru.kotletkin.entityscout.search.dto.SearchSingleRequest;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/single")
    public SearchSingleDTO searchSingle(@Valid @RequestBody SearchSingleRequest searchSingleRequest) {
        return searchService.searchBySingleRequest(searchSingleRequest);
    }
}
