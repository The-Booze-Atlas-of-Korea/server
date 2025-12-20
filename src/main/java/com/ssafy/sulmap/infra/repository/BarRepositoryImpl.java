package com.ssafy.sulmap.infra.repository;

import com.ssafy.sulmap.core.model.BarListItemModel;
import com.ssafy.sulmap.core.model.BarModel;
import com.ssafy.sulmap.core.model.query.NearbyBarsQuery;
import com.ssafy.sulmap.core.repository.BarRepository;
import com.ssafy.sulmap.infra.external.elastic.BarSearchElasticClient;
import com.ssafy.sulmap.infra.mapper.BarMapper;
import com.ssafy.sulmap.infra.model.BarSearchElasticEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class BarRepositoryImpl implements BarRepository {

    private final BarMapper _barMapper;
    private final BarSearchElasticClient _barSearchElasticClient;

    @Override
    public Optional<BarModel> findById(long barId) {
        var res = _barMapper.selectById(barId);
        if(res.isEmpty())
            return Optional.empty();
        var categoryEntities = _barMapper.selectBarCategory(barId);
        var model = res.get().toBarModel(categoryEntities);
        return Optional.of(model);
    }

    @Override
    public List<BarListItemModel> findNearby(NearbyBarsQuery query) throws IOException {
        var res = _barSearchElasticClient.findNearby(query);
        return res.stream().map(BarSearchElasticEntity::toBarListItemModel).toList();
    }
}
