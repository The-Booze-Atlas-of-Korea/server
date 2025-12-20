package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.BarListItemModel;
import com.ssafy.sulmap.core.model.BarModel;
import com.ssafy.sulmap.core.model.query.NearbyBarsQuery;
import com.ssafy.sulmap.core.repository.BarRepository;
import com.ssafy.sulmap.core.service.BarService;
import com.ssafy.sulmap.share.result.Result;
import com.ssafy.sulmap.share.result.error.impl.NotFoundError;
import com.ssafy.sulmap.share.result.error.impl.SimpleError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BarServiceImpl implements BarService {
    //@todo delete when develop BarRepository
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final BarRepository _barRepository;


    @Override
    public Result<List<BarListItemModel>> findNearbyBars(NearbyBarsQuery query) {
        try {
            var result = _barRepository.findNearby(query);
            return Result.ok(result);
        }
        catch (Exception e) {
            return Result.fail(SimpleError.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Internal server error")
                    .metadata(Map.of("exception", e.getClass().getSimpleName()))
                    .cause(e)
                    .build());
        }
    }

    @Override
    public Result<BarModel> findBarById(long id) {
        var result =  _barRepository.findById(id);
        if(result.isEmpty() || result.get().getDeletedAt() != null){
            return Result.fail(new NotFoundError("id", id));
        }
        return Result.ok(result.get());
    }
}
