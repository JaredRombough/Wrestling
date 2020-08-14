package openwrestling.database;

import ma.glasnost.orika.CustomMapper;
import openwrestling.entities.SegmentEntity;
import openwrestling.model.gameObjects.Segment;

public class SegmentMapper extends CustomMapper<SegmentEntity, Segment> {
//    public void mapAtoB(SegmentEntity a, Segment b, MappingContext context) {
//        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
//        mapperFactory.getConverterFactory().registerConverter(new LocalDateConverter());
//        mapperFactory.getMapperFacade().map(b, SegmentEntity.class);
//        if (CollectionUtils.isNotEmpty(b.getSegmentTeams()) && CollectionUtils.isEmpty(b.getSegmentTeams().get(0).getWorkers())) {
//            b.setSegmentTeams(
//                    a.getTeams().stream().map(segmentTeamEntity -> {
//
//                        List<Worker> workers = segmentTeamEntity.getSegmentTeamWorkers().stream()
//                                .map(segmentTeamWorkerEntity -> {
//                                    WorkerEntity workerEntity = segmentTeamWorkerEntity.getWorkerEntity();
//                                    return mapperFactory.getMapperFacade().map(workerEntity, Worker.class);
//                                })
//                                .collect(Collectors.toList());
//                        SegmentTeam segmentTeam = mapperFactory.getMapperFacade().map(segmentTeamEntity, SegmentTeam.class);
//                        segmentTeam.setWorkers(workers);
//                        return segmentTeam;
//                    }).collect(Collectors.toList())
//            );
//        }
//    }
//
//    public void mapBtoA(Segment a, SegmentEntity b, MappingContext context) {
//        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
//        mapperFactory.getConverterFactory().registerConverter(new LocalDateConverter());
//        b = mapperFactory.getMapperFacade().map(a, SegmentEntity.class);
//        Object[] list = context.getDestinationObjects();
//        int i = 0;
//    }
}