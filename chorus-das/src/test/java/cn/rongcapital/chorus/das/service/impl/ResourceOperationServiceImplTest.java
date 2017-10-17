package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.NoSuchStatusCodeException;
import cn.rongcapital.chorus.common.exception.ResourceNotEnoughException;
import cn.rongcapital.chorus.das.dao.ResourceInnerMapper;
import cn.rongcapital.chorus.das.dao.ResourceOperationDOMapper;
import cn.rongcapital.chorus.das.dao.ResourceOperationMapper;
import cn.rongcapital.chorus.das.dao.TotalResourceDOMapper;
import cn.rongcapital.chorus.das.entity.ResourceApproveDO;
import cn.rongcapital.chorus.das.entity.ResourceOperation;
import cn.rongcapital.chorus.das.entity.TotalResource;
import cn.rongcapital.chorus.das.service.impl.ResourceOperationServiceImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by abiton on 15/11/2016.
 */
public class ResourceOperationServiceImplTest {
    @Mock
    ResourceOperationMapper operationMapper;
    @Mock
    ResourceOperationDOMapper operationDOMapper;
    @Mock
    ResourceInnerMapper resourceInnerMapper;
    @Mock
    TotalResourceDOMapper totalResourceMapper;

    @InjectMocks
    ResourceOperationServiceImpl resourceOperationService;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void applyResourceForProject() {


    }

    @Test
    public void approveResourceWithApprove() throws ResourceNotEnoughException, NoSuchStatusCodeException {
        ResourceOperation operation = new ResourceOperation();
        operation.setCpu(1);
        operation.setMemory(2);
        operation.setStorage(3);
        operation.setResourceId(null);
        Long operationId = 1L;
        when(operationMapper.selectByPrimaryKey(operationId)).thenReturn(operation);
        when(resourceInnerMapper.insertSelective(any())).thenReturn(1);
        TotalResource total = new TotalResource();
        total.setCpu(100);
        total.setMemory(1000);
        total.setStorage(1000);
        TotalResource used = new TotalResource();
        used.setCpu(10);
        used.setMemory(100);
        used.setStorage(100);
        when(totalResourceMapper.sumResourceByStatus(eq(StatusCode.RESOURCE_APPROVE.getCode()))).thenReturn(used);
        when(operationMapper.updateByPrimaryKeySelective(operation)).thenReturn(1);

        String notes = "";
        ResourceApproveDO approveDO = new ResourceApproveDO();
        approveDO.setLeftResource(used);
        approveDO.setApprove(true);
        approveDO.setNotes(notes);
        approveDO.setOperationId(operationId);
        approveDO.setUserId("userId");
        approveDO.setUserName("userName");
        resourceOperationService.approveResource(approveDO);

        verify(operationMapper, times(1)).selectByPrimaryKey(eq(1L));
        verify(totalResourceMapper,times(1)).sumResourceByStatus(eq(StatusCode.RESOURCE_APPROVE.getCode()));
        verify(operationMapper,times(1)).updateByPrimaryKeySelective(eq(operation));
    }

    @Test(expected = ResourceNotEnoughException.class)
    public void approveResourceWithResourceNotEnoughException() throws ResourceNotEnoughException, NoSuchStatusCodeException {
        ResourceOperation operation = new ResourceOperation();
        operation.setCpu(1000);
        operation.setMemory(2);
        operation.setStorage(3);
        operation.setResourceId(null);
        Long operationId = 1L;
        when(operationMapper.selectByPrimaryKey(operationId)).thenReturn(operation);
        when(resourceInnerMapper.insertSelective(any())).thenReturn(1);
        TotalResource total = new TotalResource();
        total.setCpu(100);
        total.setMemory(1000);
        total.setStorage(1000);
        TotalResource used = new TotalResource();
        used.setCpu(10);
        used.setMemory(100);
        used.setStorage(100);
        when(totalResourceMapper.sumResourceByStatus(eq(StatusCode.RESOURCE_APPROVE.getCode()))).thenReturn(used);
        when(operationMapper.updateByPrimaryKeySelective(operation)).thenReturn(1);

        String notes = "";
        ResourceApproveDO approveDO = new ResourceApproveDO();
        approveDO.setLeftResource(used);
        approveDO.setApprove(true);
        approveDO.setNotes(notes);
        approveDO.setOperationId(operationId);
        approveDO.setUserId("userId");
        approveDO.setUserName("userName");
        resourceOperationService.approveResource(approveDO);
    }


    @Test(expected = NoSuchStatusCodeException.class)
    public void approveResourceWithNoSuchStatusCodeException() throws ResourceNotEnoughException, NoSuchStatusCodeException {
        ResourceOperation operation = new ResourceOperation();
        operation.setCpu(1000);
        operation.setMemory(2);
        operation.setStorage(3);
        operation.setResourceId(null);
        Long operationId = 1L;
        when(operationMapper.selectByPrimaryKey(operationId)).thenReturn(operation);
        when(resourceInnerMapper.insertSelective(any())).thenReturn(1);
        TotalResource total = new TotalResource();
        total.setCpu(100);
        total.setMemory(1000);
        total.setStorage(1000);
        TotalResource used = new TotalResource();
        used.setCpu(10);
        used.setMemory(100);
        used.setStorage(100);
        when(totalResourceMapper.sumResourceByStatus(eq(StatusCode.RESOURCE_APPROVE.getCode()))).thenReturn(used);
        when(operationMapper.updateByPrimaryKeySelective(operation)).thenReturn(1);

        ResourceApproveDO approveDO = new ResourceApproveDO();
        approveDO.setLeftResource(used);
        approveDO.setApprove(true);
        approveDO.setOperationId(operationId);
        approveDO.setUserId("userId");
        approveDO.setUserName("userName");
        resourceOperationService.approveResource(approveDO);
    }


}
