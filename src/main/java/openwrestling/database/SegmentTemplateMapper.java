package openwrestling.database;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import openwrestling.entities.SegmentEntity;
import openwrestling.entities.SegmentTemplateEntity;
import openwrestling.entities.WorkerEntity;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.SegmentTemplate;
import openwrestling.model.gameObjects.Worker;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SegmentTemplateMapper extends CustomMapper<SegmentTemplateEntity, SegmentTemplate> {
    public void mapAtoB(SegmentTemplateEntity a, SegmentTemplate b, MappingContext context) {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.getConverterFactory().registerConverter(new LocalDateConverter());
        mapperFactory.getMapperFacade().map(b, SegmentEntity.class);

        if (CollectionUtils.isNotEmpty(b.getSegmentTeams()) && CollectionUtils.isEmpty(b.getSegmentTeams().get(0).getWorkers())) {
            b.setSegmentTeams(
                    a.getSegmentTeams().stream().map(segmentTeamEntity -> {
                        List<Worker> workers = segmentTeamEntity.getSegmentTeamWorkers().stream()
                                .map(segmentTeamWorkerEntity -> {
                                    WorkerEntity workerEntity = segmentTeamWorkerEntity.getWorkerEntity();
                                    return mapperFactory.getMapperFacade().map(workerEntity, Worker.class);
                                })
                                .collect(Collectors.toList());
                        SegmentTeam segmentTeam = mapperFactory.getMapperFacade().map(segmentTeamEntity, SegmentTeam.class);
                        segmentTeam.setWorkers(workers);
                        return segmentTeam;
                    }).collect(Collectors.toList())
            );
        }
    }
};