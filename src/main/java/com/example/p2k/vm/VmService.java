package com.example.p2k.vm;

import com.example.p2k._core.exception.Exception400;
import com.example.p2k._core.exception.Exception404;
import com.example.p2k._core.web.AdminConstants;
import com.example.p2k.course.Course;
import com.example.p2k.course.CourseRepository;
import com.example.p2k.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VmService {

    private final VmRepository vmRepository;
    private final CourseRepository courseRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RestTemplate restTemplate;
    private final ObjectMapper ob = new ObjectMapper();
    private int portnum = 6100;
    private int nodePort = 30000; // 30000~32768까지 사용 가능
    private final String baseImagePath = "registry.p2kcloud.com/base/1/kasmweb:v1";

    //private final String baseURL = "http://175.45.203.51:5000"; // k8s에게 명령을 내리는 서버 - test용
    private final String baseURL = "http://223.130.137.170:5000"; // k8s에게 명령을 내리는 서버 - 실제 서버용
    //private final String baseURL = "http://localhost:5000";

    @Transactional
    public Vm findById(Long id) {
        Vm vm = vmRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 가상환경을 찾을 수 없습니다.")
        );

        return vm;
    }

    @Transactional
    public VmResponse.FindAllDTO findAllByUserId(Long id) {
        List<Vm> vmList = vmRepository.findAllByUserId(id);
        return new VmResponse.FindAllDTO(vmList);
    }

    @Transactional
    public VmResponse.FindAllDTO findAll() {
        List<Vm> vmList = vmRepository.findAll(true);
        return new VmResponse.FindAllDTO(vmList);
    }

    @Transactional
    public VmResponse.FindAllDTO findAllByKeyword(String keyword) {
        List<Vm> vmList = vmRepository.findAllByKeyword(keyword);
        return new VmResponse.FindAllDTO(vmList);
    }

    @Transactional
    public void save(Vm vm) {
        vmRepository.save(vm);
    }

    @Transactional
    public void update(Long id, VmRequest.UpdateDTO requestDTO) throws Exception {
       //vmRepository.update(id, requestDTO.getName(), requestDTO.getDescription(), requestDTO.getCourseId());

        Vm vm = vmRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 가상환경은 존재하지 않습니다.")
        );

        if (requestDTO.getCourseId()==null) {
            vm.update(requestDTO, null);
        } else {
            Course course = courseRepository.findById(requestDTO.getCourseId()).orElseThrow(
                    () -> new Exception404("해당 강좌는 존재하지 않습니다.")
            );
            vm.update(requestDTO, course);
        }
    }

    @Transactional
    public void create(User user, VmRequest.CreateDTO requestDTO) throws Exception {

        List<Vm> vms = vmRepository.findAllByUserId(user.getId());
        System.out.println("vms.size() = " + vms.size());
        if (vms.size() > AdminConstants.VM_MAX_NUM) {
            System.out.println("가상환경은 최대 3개까지 생성할 수 있습니다." );
            throw new Exception400("가상환경은 최대 3개까지 생성할 수 있습니다.");
        }

        // 요청을 보낼 flask url
        String url = baseURL+"/create";

        // 암호화한 key
        String key = bCryptPasswordEncoder.encode(user.getPassword());

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, pwd
        VmRequestStF.createDTO requestDTOStF = new VmRequestStF.createDTO();
        requestDTOStF.setId(user.getId());
        requestDTOStF.setPort(portnum);
        requestDTOStF.setNodePort(nodePort);
        requestDTOStF.setImagePath(baseImagePath);
        requestDTOStF.setPassword(requestDTO.getPassword());
        requestDTOStF.setScope(requestDTO.getScope());
        requestDTOStF.setControl(requestDTO.getControl());

        // json을 string으로
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr, headers);

        // restTemplate 이용해 요청 보내고 응답 받아옴
        ResponseEntity<?> response = restTemplate.postForEntity(url, entity, VmResponseFtS.createDTO.class);
        String responseBody = ob.writeValueAsString(response.getBody());

        System.out.println("response: "+response);
        System.out.println("response.getBody: "+response.getBody());
        System.out.println("responseBody: "+responseBody);

        VmResponseFtS.createDTO res = ob.readValue(responseBody, VmResponseFtS.createDTO.class);
        System.out.println("res = " + res.getContainerId());
        System.out.println("res.getImageId() = " + res.getImageId());
        System.out.println("res: "+ res);
        System.out.println("res.getIP: " + res.getExternalNodeIp());

        // flask에서 받은 응답으로 가상환경 생성하고 저장
        Vm vm = Vm.builder()
                .vmname(requestDTO.getVmname())
                .password(requestDTO.getPassword())
                .description(requestDTO.getDescription())
                .scope(requestDTO.getScope().booleanValue())
                .control(requestDTO.getControl().booleanValue())
                .user(user)
                .port(portnum)
                .nodePort(nodePort)
                .externalNodeIp(res.getExternalNodeIp())
                .containerId(res.getContainerId())
                .imageId("")
                .state("stop")
                .build();

        vmRepository.save(vm);
        portnum+=1;
        nodePort+=1;
    }

    @Transactional
    public void load(User user, VmRequest.LoadDTO requestDTO) throws Exception {

        // 요청을 보낼 flask url
        String url = baseURL+"/load";

        // 암호화한 key
        String key = bCryptPasswordEncoder.encode(user.getPassword());

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, pwd
        VmRequestStF.loadDTO requestDTOStF = new VmRequestStF.loadDTO();
        requestDTOStF.setId(user.getId());
        requestDTOStF.setPort(portnum);
        requestDTOStF.setNodePort(nodePort);
        requestDTOStF.setPassword(requestDTO.getPassword());
        requestDTOStF.setScope(requestDTO.getScope());
        requestDTOStF.setControl(requestDTO.getControl());
        requestDTOStF.setKey(requestDTO.getKey()); // 로드할 이미지의 id (지금은 key라고함)
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr, headers);

        // restTemplate 이용해 요청 보내고 응답 받아옴
        ResponseEntity<?> response = restTemplate.postForEntity(url, entity, VmResponseFtS.loadDTO.class);
        String responseBody = ob.writeValueAsString(response.getBody());
        VmResponseFtS.loadDTO res = ob.readValue(responseBody, VmResponseFtS.loadDTO.class);

        // 임시로 제어권은 true로 설정
        if (requestDTO.getControl()==null) {
            requestDTO.setControl(true);
        }

        // flask에서 받은 응답으로 가상환경 생성하고 저장
        Vm vm = Vm.builder()
                .vmname(requestDTO.getName())
                .password(requestDTO.getPassword())
                .scope(requestDTO.getScope().booleanValue())
                .control(requestDTO.getControl().booleanValue())
                .user(user)
                .port(portnum)
                .nodePort(nodePort)
                .externalNodeIp(res.getExternalNodeIp())
                .state("stop")
                .imageId("")
                .vmKey(key)
                .build();

        vmRepository.save(vm);
        portnum+=1;
        nodePort+=1;

    }

    @Transactional
    public void start(Long id) throws Exception {

        // 요청을 보낼 flask url
        String url = baseURL+"/start";

        // 시작할 가상환경 찾기
        Vm vm = vmRepository.findById(id).orElse(null);

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, 컨테이너id
        VmRequestStF.startDTO requestDTOStF = new VmRequestStF.startDTO();
        requestDTOStF.setPort(vm.getPort());
        requestDTOStF.setNodePort(vm.getNodePort());
        requestDTOStF.setContainerId(vm.getContainerId());
        requestDTOStF.setPassword(vm.getPassword());
        requestDTOStF.setScope(vm.getScope());
        requestDTOStF.setControl(vm.getControl());
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr ,headers);

        // flask로 요청보냄
        restTemplate.postForEntity(url, entity, VmResponseFtS.startDTO.class);

        vm.updateState("running");
    }

    @Transactional
    public void stop(Long id) throws Exception {

        // 요청을 보낼 flask url
        String url = baseURL+"/stop";

        // 중지할 가상환경 찾기
        Vm vm = vmRepository.findById(id).orElse(null);

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, 컨테이너id
        VmRequestStF.stopDTO requestDTOStF = new VmRequestStF.stopDTO();
        requestDTOStF.setPort(vm.getPort());
        requestDTOStF.setNodePort(vm.getNodePort());

        requestDTOStF.setContainerId(vm.getContainerId());
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr ,headers);

        // flask로 요청보냄
        restTemplate.postForEntity(url, entity, VmResponseFtS.stopDTO.class);

        vm.updateState("stop");
    }

    @Transactional
    public void save(User user, Long id) throws Exception {

        // 요청을 보낼 flask url
        String url = baseURL+"/save";

        // 저장할 가상환경 찾기
        Vm vm = vmRepository.findById(id).orElse(null);

        // requestDTO header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // requestDTO body 만들기 - port, 컨테이너id
        VmRequestStF.saveDTO requestDTOStF = new VmRequestStF.saveDTO();
        requestDTOStF.setId(user.getId());
        requestDTOStF.setPort(vm.getPort());
        requestDTOStF.setNodePort(vm.getNodePort());
        requestDTOStF.setPwd(vm.getPassword());
        requestDTOStF.setImageId(vm.getImageId());
        requestDTOStF.setContainerId(vm.getContainerId());
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        // header, body로 requestDTO 만들기
        HttpEntity<String> entity = new HttpEntity<>(jsonStr ,headers);

        // flask로 요청보냄
        ResponseEntity<?> response = restTemplate.postForEntity(url, entity, VmResponseFtS.saveDTO.class);
        String responseBody = ob.writeValueAsString(response.getBody());
        VmResponseFtS.saveDTO res = ob.readValue(responseBody, VmResponseFtS.saveDTO.class);
        System.out.println("res = " + res);
        System.out.println("res.getContainerId() = " + res.getContainerId());
        System.out.println("res.getImageId() = " + res.getImageId());

        // 새로운 containerid, imageid 업데이트
        vm.updateContainerId(res.getContainerId());
        vm.updateLoadKey(res.getLoadKey());
    }

    @Transactional
    public void delete(User user, Long id) throws Exception {

        String url = baseURL+"/delete";

        Vm vm = vmRepository.findById(id).orElse(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        VmRequestStF.deleteDTO requestDTOStF = new VmRequestStF.deleteDTO();
        requestDTOStF.setId(user.getId());
        requestDTOStF.setPort(vm.getPort());
        requestDTOStF.setNodePort(vm.getNodePort());
        requestDTOStF.setContainerId(vm.getContainerId());
        requestDTOStF.setImageId(vm.getImageId());
        String jsonStr = ob.writeValueAsString(requestDTOStF);

        HttpEntity<String> entity = new HttpEntity<String>(jsonStr ,headers);

        restTemplate.postForEntity(url, entity, VmResponseFtS.deleteDTO.class);

        vmRepository.deleteById(id);
    }

}