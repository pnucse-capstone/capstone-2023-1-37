from flask import Flask, render_template, request, jsonify
import os, time
import func

app = Flask(__name__)

baseImageId = '35279a3a953f' # tar가 설치된 kasm-1.14.0 이미지


BS = 16
pad = (lambda s: s + (BS - len(s) % BS) * chr(BS - len(s) % BS).encode())
unpad = (lambda s: s[:-ord(s[len(s)-1:])])

# spring 서버에서 컨테이너 생성 요청이 왔을 때, base 이미지로 컨테이너 생성하고 이미지 저장
@app.route('/create', methods=['POST'])
def create():

    requestDTO = request.get_json()
    print("[create requestDTO] ", requestDTO)
    userId, port, pwd = str(requestDTO['id']), str(requestDTO['port']), str(requestDTO['password'])
    scope, control = str(requestDTO['scope']), str(requestDTO['control'])

    stream1 = os.popen(func.createContainerCmd(port, pwd, baseImageId))
    containerId = stream1.read()[:12]
    time.sleep(5)
    enContainerId = func.aes.encrypt(containerId) # containerId 암호화

    vmName = "vm"+port
    scriptPath = "/dockerstartup/start.sh"
    nodePort = str(requestDTO['nodePort'])
    imagePath = str(requestDTO['imagePath'])

    os.popen(func.startContainerCmd(containerId))
    os.popen(func.copyScriptToContainer(containerId))
    os.popen(func.stopContainerCmd(containerId))
    os.popen(func.deleteContainerCmd(containerId))

    # PV yaml 파일 생성
    pvPodYaml = func.generatePVPodYaml(vmName, vmName, vmName)
    pvFilePath = "/home/yaml/"+vmName+"PV.yaml"
    with open(pvFilePath, 'w') as pvYamlFile:
        pvYamlFile.write(pvPodYaml)

    # PVC yaml 파일 생성
    pvcPodYaml = func.generatePVCPodYaml(vmName, vmName)
    pvcFilePath = "/home/yaml/"+vmName+"PVC.yaml"
    with open(pvcFilePath, 'w') as pvcYamlFile:
        pvcYamlFile.write(pvcPodYaml)

    # Depolyment yaml 파일 생성
    deploymentPodYaml = func.generateDeploymentPodYaml(vmName, vmName, imagePath, port, vmName, vmName, vmName)
    deploymentFilePath = "/home/yaml/"+vmName+"Deployment.yaml"
    with open(deploymentFilePath, 'w') as deploymentYamlFile:
        deploymentYamlFile.write(deploymentPodYaml)

    # Service yaml 파일 생성
    servicePodYaml = func.generateServiceYaml(vmName, port, nodePort)
    serviceFilePath = "/home/yaml/"+vmName+"Service.yaml"
    with open(serviceFilePath, 'w') as serviceYamlFile:
        serviceYamlFile.write(servicePodYaml)

    print(deploymentPodYaml)
    print(servicePodYaml)

    response = {
            'port': port,
            'containerId' : enContainerId,
            'imageId' : enContainerId,#enImageId,
        }

    return jsonify(response), 200



#spring 서버에서 가상환경 로드했을 때, 이미지로 컨테이너 생성 후 새로운 이미지로 저장
@app.route('/load', methods=['POST'])
def load() :

    requestDTO = request.get_json()
    print("[load requestDTO] ", requestDTO)
    userId, port, pwd, imageId = str(requestDTO['id']), str(requestDTO['port']), str(requestDTO['password']), str(requestDTO['key'])
    loadKey = str(requestDTO['key'])
    deloadKey = func.aes.decrypt(loadKey)
    scope, control = str(requestDTO['scope']), str(requestDTO['control'])
    vmName = "vm"+port
    nodePort = str(requestDTO['nodePort'])

    # PV yaml 파일 생성
    pvPodYaml = func.generatePVPodYaml(vmName, vmName, vmName)
    pvFilePath = "/home/yaml/"+vmName+"PV.yaml"
    with open(pvFilePath, 'w') as pvYamlFile:
        pvYamlFile.write(pvPodYaml)

    # PVC yaml 파일 생성
    pvcPodYaml = func.generatePVCPodYaml(vmName, vmName)
    pvcFilePath = "/home/yaml/"+vmName+"PVC.yaml"
    with open(pvcFilePath, 'w') as pvcYamlFile:
        pvcYamlFile.write(pvcPodYaml)

    # Depolyment yaml 파일 생성
    deploymentPodYaml = func.generateDeploymentPodYaml(vmName, vmName, deloadKey, port, vmName, vmName, vmName)
    deploymentFilePath = "/home/yaml/"+vmName+"Deployment.yaml"
    with open(deploymentFilePath, 'w') as deploymentYamlFile:
        deploymentYamlFile.write(deploymentPodYaml)

    # Service yaml 파일 생성
    servicePodYaml = func.generateServiceYaml(vmName, port, nodePort)
    serviceFilePath = "/home/yaml/"+vmName+"Service.yaml"
    with open(serviceFilePath, 'w') as serviceYamlFile:
        serviceYamlFile.write(servicePodYaml)

    print(deploymentPodYaml)
    print(servicePodYaml)

    response = {
            'port': port,
        }


    return jsonify(response), 200



# spring 서버에서 컨테이너 실행 요청이 왔을 때, 컨테이너 실행
@app.route('/start', methods=['POST'])
def start():

    print("hello")
    print(request.get_json())

    requestDTO = request.get_json()
    print("[start requestDTO] ", requestDTO)
    port, containerId = str(requestDTO['port']), str(requestDTO['containerId'])
    pwd = str(requestDTO['password'])
    scope, control = str(requestDTO['scope']), str(requestDTO['control'])

    vmName = "vm"+port

    pvFilePath = "/home/yaml/"+vmName+"PV.yaml"
    pvcFilePath = "/home/yaml/"+vmName+"PVC.yaml"
    deploymentFilePath = "/home/yaml/"+vmName+"Deployment.yaml"
    serviceFilePath = "/home/yaml/"+vmName+"Service.yaml"

    nodes = func.extractNodeCPUAndMemory()
    if len(nodes) >= 2:
        maxMemUse, maxMemUseNode = func.findMaxMemNodes(nodes)
        print("maxMemUseNodee: ", maxMemUseNode)
        print("\n maxMemUse: ", maxMemUse)

        os.popen("kubectl cordon " + maxMemUseNode + " --kubeconfig /root/kubeconfig.yml") # 스케줄 불가로 만들기 - 더이상 pod 할당 안되게

        time.sleep(30) # 30초 대기

        result = os.popen("kubectl get nodes " + maxMemUseNode + " --kubeconfig /root/kubeconfig.yml").read()

        print("result:", result)

        nodeInfo = result.split('\n')[1:-1]
        status = nodeInfo[0].split()[1]

        print("status: ", status)

    os.popen(func.applyPodCmd(pvFilePath))
    os.popen(func.applyPodCmd(pvcFilePath))
    os.popen(func.applyPodCmd(deploymentFilePath))
    os.popen(func.applyPodCmd(serviceFilePath))

    time.sleep(30)

    os.popen("kubectl uncordon " + maxMemUseNode + " --kubeconfig /root/kubeconfig.yml") # 다시 풀어주기

    stream1 = os.popen(func.getPodName(port))
    podName = stream1.read()[4:-1]

    print("podName:", podName)

    os.popen(func.copyScriptToPod(podName, vmName))

    time.sleep(1)

    changeVncScopeAndControlCmd = "kubectl exec -it "+podName+" bash /tmp/start.sh "+scope+" "+control+" "+pwd+" --kubeconfig /root/kubeconfig.yml"
    os.popen(changeVncScopeAndControlCmd)

    time.sleep(2)

    nodeList = func.extractNodeInfo()
    time.sleep(30)
    externalNodeIp = func.extractNodeIpOfPod(nodeList)

    print("nodes: ", nodeList)
    print("externalIp: ", externalNodeIp)

    response = {
            'port' : port,
            'containerId' : containerId,
            'externalNodeIp': externalNodeIp

        }

    return jsonify(response), 200



# spring 서버에서 컨테이너 중지 요청이 왔을 때, 컨테이너 중지
@app.route('/stop', methods=['POST'])
def stop():

    requestDTO = request.get_json()
    print("[stop requestDTO] ", requestDTO)
    port, containerId = str(requestDTO['port']), str(requestDTO['containerId'])
    vmName = "vm"+port # containerName과 동일

    podName = os.popen(func.getPodName(port)).read()[4:-1]
    namespace = os.popen(func.getPodNameSpace(podName)).read()[:-1]
    print("pod: "+podName)
    print("name: "+namespace)

    os.popen(f"mkdir /home/dockerFile/backup/{vmName}")

    print("start")
    accessContainer = f"kubectl exec -n {namespace} {podName} --kubeconfig /root/kubeconfig.yml -- tar cf - /home/kasm-user/ . | tar xf - -C /home/dockerFile/backup/{vmName}/"
    os.popen(accessContainer)

    time.sleep(30)

    func.deleteDeployPodCmd(vmName)
    func.deleteServicePodCmd(vmName)

    time.sleep(2)

    print("end")

    response = {
            'port' : port,
            'containerId' : containerId
        }

    return jsonify(response), 200



# spring 서버에서 컨테이너 저장 요청이 왔을 때, 현재 컨테이너의 이미지 생성 -> 기존 이미지 삭제 -> push
@app.route('/save', methods=['POST'])
def save() :

    requestDTO = request.get_json()
    print("[save requestDTO] ", requestDTO)
    userId, port, pwd = str(requestDTO['id']), str(requestDTO['port']), str(requestDTO['pwd'])
    containerId, imageId = str(requestDTO['containerId']), str(requestDTO['imageId'])

    vmName = "vm"+port
    imagePath = "registry.p2kcloud.com/base/"+userId
    podName = os.popen(func.getPodName(port)).read()[4:-1]
    loadKey = func.aes.encrypt(imagePath+":"+port)

    namespace = os.popen(func.getPodNameSpace(podName)).read()[:-1]

    stream1 = os.popen(func.createContainerCmd(port, pwd, baseImageId))
    containerId = stream1.read()[:12]
    time.sleep(5)

    os.popen(func.startContainerCmd(containerId))

    os.popen(func.copyDesktopToContainer(containerId, vmName))

    time.sleep(20)

    stream2 = os.popen(func.createImgCmd(containerId, userId, port))
    newImageId = stream2.read()[7:20]
    print("stream2 : ", stream2.read())
    print("newimageId : ", newImageId)

    stream3 = os.popen(func.pushImgCmd(userId, port))
    print("stream3 : ", stream3.read())

    os.popen(func.deleteContainerCmd(containerId))
    os.popen(f"docker rmi registry.p2kcloud.com/base/{userId}:{port}")

    time.sleep(2)

    print("path: "+imagePath+":"+port)

    response = {
            'containerId' : containerId,
            'imageId' : imageId,
            'loadKey' : loadKey
        }

    return jsonify(response), 200



# spring 서버에서 컨테이너 삭제 요청이 왔을 때, 컨테이너, 이미지 삭제
@app.route('/delete', methods=['POST'])
def delete():

    requestDTO = request.get_json()
    print("[delete requestDTO] ", requestDTO)
    userId, port = str(requestDTO['id']), str(requestDTO['port'])
    containerId, imageId = str(requestDTO['containerId']), str(requestDTO['imageId'])

    vmName = "vm"+port

    pvFilePath = "/home/yaml/"+vmName+"PV.yaml"
    pvcFilePath = "/home/yaml/"+vmName+"PVC.yaml"
    deploymentFilePath = "/home/yaml/"+vmName+"Deployment.yaml"
    serviceFilePath = "/home/yaml/"+vmName+"Service.yaml"

    os.popen(func.deleteYamlFile(pvFilePath))
    os.popen(func.deleteYamlFile(pvcFilePath))
    os.popen(func.deleteYamlFile(deploymentFilePath))
    os.popen(func.deleteYamlFile(serviceFilePath))
    os.popen(f"rm -rf /home/dockerFile/backup/{vmName}")
    os.popen(f"rm /home/dockerFile/{vmName}")

    os.popen(func.deletePVPodCmd(vmName))
    os.popen(func.deletePVCPodCmd(vmName))
    os.popen(func.deleteDeployPodCmd(vmName))
    os.popen(func.deleteServicePodCmd(vmName))


    response = {
            'port' : port,
            'containerId' : containerId
        }

    return jsonify(response), 200

if __name__ == '__main__':

    app.run('0.0.0.0', port=5000, debug=True)