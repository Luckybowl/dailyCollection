# 1、Kubernetes概述

Kubernetes简称k8s，是一个开源的，用于管理云平台中多个主机上的容器化的应用。

Kubernetes提供了应用部署，规划，更新，维护的一种机制。

Kubernetes一个核心的特点就是能够自主的管理容器来保证云平台中的容器按照用户的期望状态运行着（比如用户想让nginx一直运行，用户不需要关心怎么去做，Kubernetes会自动去监控，然后去重启，新建，总之，让nginx一直提供服务）



# 2、基础架构

 ![](D:\Users\pchenc\MD图片\9d064a04-cb11-4715-841d-ebd958edb031.png)



## Kubernetes节点

在这张系统架构图中，我们把服务分为运行在**工作节点**上的服务和组成**集群**级别控制板的服务。

Kubernetes节点有运行应用容器必备的服务，而这些都是受Master的控制。

每个节点上当然都要运行Docker。Docker来负责所有具体的镜像下载和容器运行。

Kubernetes主要由以下几个核心组件组成：

- etcd保存了整个集群的状态；
- apiserver提供了资源操作的唯一入口，并提供认证、授权、访问控制、API注册和发现等机制；
- controller manager负责维护集群的状态，比如故障检测、自动扩展、滚动更新等；
- scheduler负责资源的调度，按照预定的调度策略将Pod调度到相应的机器上；
- kubelet负责维护容器的生命周期，同时也负责Volume（CVI）和网络（CNI）的管理；
- Container runtime负责镜像管理以及Pod和容器的真正运行（CRI）；
- kube-proxy负责为Service提供cluster内部的服务发现和负载均衡；

除了核心组件，还有一些推荐的Add-ons：

- kube-dns负责为整个集群提供DNS服务
- Ingress Controller为服务提供外网入口
- Heapster提供资源监控
- Dashboard提供GUI
- Federation提供跨可用区的集群
- Fluentd-elasticsearch提供集群日志采集、存储与查询

## 2.1 Master

![](D:\Users\pchenc\MD图片\master.png)

　　Master节点上面主要由四个模块组成：APIServer、scheduler、controller manager、etcd。

　　　　**APIServer**。APIServer负责对外提供RESTful的Kubernetes API服务，它是系统管理指令的统一入口，任何对资源进行增删改查的操作都要交给APIServer处理后再提交给etcd。如架构图中所示，kubectl（Kubernetes提供的客户端工具，该工具内部就是对Kubernetes API的调用）是直接和APIServer交互的。

　　　　**schedule**。scheduler的职责很明确，就是负责调度pod到合适的Node上。如果把scheduler看成一个黑匣子，那么它的输入是pod和由多个Node组成的列表，输出是Pod和一个Node的绑定，即将这个pod部署到这个Node上。Kubernetes目前提供了调度算法，但是同样也保留了接口，用户可以根据自己的需求定义自己的调度算法。

　　　　**controller manager**。如果说APIServer做的是“前台”的工作的话，那controller manager就是负责“后台”的。每个资源一般都对应有一个控制器，而controller manager就是负责管理这些控制器的。比如我们通过APIServer创建一个pod，当这个pod创建成功后，APIServer的任务就算完成了。而后面保证Pod的状态始终和我们预期的一样的重任就由controller manager去保证了。

　　　　**etcd**。etcd是一个高可用的键值存储系统，Kubernetes使用它来存储各个资源的状态，从而实现了Restful的API。

 

## 2.2 Node

　　![](D:\Users\pchenc\MD图片\node.png)

每个Node节点主要由三个模块组成：kubelet、kube-proxy、runtime。

　　　　**runtime**。runtime指的是容器运行环境，目前Kubernetes支持docker和rkt两种容器。

　　　　**kube-proxy**。该模块实现了Kubernetes中的服务发现和反向代理功能,每一个节点也运行一个简单的网络代理和负载均衡。反向代理方面：kube-proxy支持TCP和UDP连接转发，默认基于Round Robin算法将客户端流量转发到与service对应的一组后端pod。服务发现方面，kube-proxy使用etcd的watch机制，监控集群中service和endpoint对象数据的动态变化，并且维护一个service到endpoint的映射关系，从而保证了后端pod的IP变化不会对访问者造成影响。另外kube-proxy还支持session affinity。

　　　　**kubelet**。Kubelet是Master在每个Node节点上面的agent，是Node节点上面最重要的模块，它负责维护和管理该Node上面的所有容器，images镜像、volumes、etc。但是如果容器不是通过Kubernetes创建的，它并不会管理。本质上，它负责使Pod得运行状态与期望的状态一致。

至此，Kubernetes的Master和Node就简单介绍完了。下面我们来看Kubernetes中的各种资源/对象。

 

# 3、Pod

　　Pod 是Kubernetes的基本操作单元，也是应用运行的载体。整个Kubernetes系统都是围绕着Pod展开的，比如如何部署运行Pod、如何保证Pod的数量、如何访问Pod等。另外，Pod是一个或多个机关容器的集合，这可以说是一大创新点，提供了一种容器的组合的模型。

### 3.1 基本操作

###  

| 创建 | kubectl create -f xxx.yaml                                  |
| ---- | ----------------------------------------------------------- |
| 查询 | kubectl get pod yourPodNamekubectl describe pod yourPodName |
| 删除 | kubectl delete pod yourPodName                              |
| 更新 | kubectl replace /path/to/yourNewYaml.yaml                   |

### 3.2 Pod与Container

　　在Docker中，容器是最小的处理单元，增删改查的对象是容器，容器是一种虚拟化技术，容器之间是隔离的，隔离是基于Linux Namespace实现的。

​	从而使得容器可以很方便的在任何地方运行。由于容器体积小且启动快，因此可以在每个容器镜像中打包一个应用程序。这种一对一的应用镜像关系拥有很多好处。使用容器，
不需要与外部的基础架构环境绑定, 因为每一个应用程序都不需要外部依赖，更不需要与外部的基础架构环境依赖。完美解决了从开发到生产环境的一致性问题。
​	容器同样比虚拟机更加透明，这有助于监测和管理。尤其是容器进程的生命周期由基础设施管理，而不是由容器内的进程对外隐藏时更是如此。

​	而在Kubernetes中，Pod包含一个或者多个相关的容器，Pod可以认为是容器的一种延伸扩展，一个Pod也是一个隔离体，而Pod内部包含的一组容器又是共享的（包括PID、Network、IPC、UTS）。除此之外，Pod中的容器可以访问共同的数据卷来实现文件系统的共享。

### 3.3 镜像

　　在kubernetes中，镜像的下载策略为：

- 　　　　**Always**：每次都下载最新的镜像
- 　　　　**Never**：只使用本地镜像，从不下载
- 　　　　**IfNotPresent**：只有当本地没有的时候才下载镜像

　　Pod被分配到Node之后会根据镜像下载策略进行镜像下载，可以根据自身集群的特点来决定采用何种下载策略。无论何种策略，都要确保Node上有正确的镜像可用。

### 3.4 其他设置

　　通过yaml文件，可以在Pod中设置：

　　　　**启动命令**，如：spec-->containers-->command；

　　　　**环境变量**，如：spec-->containers-->env-->name/value；

　　　　**端口桥接**，如：spec-->containers-->ports-->containerPort/protocol/hostIP/hostPort（使用hostPort时需要注意端口冲突的问题，不过Kubernetes在调度Pod的时候会检查宿主机端口是否冲突，比如当两个Pod均要求绑定宿主机的80端口，Kubernetes将会将这两个Pod分别调度到不同的机器上）;

　　　　**Host网络**，一些特殊场景下，容器必须要以host方式进行网络设置（如接收物理机网络才能够接收到的组播流），在Pod中也支持host网络的设置，如：spec-->hostNetwork=true；

　　　　**数据持久化**，如：spec-->containers-->volumeMounts-->mountPath;

　　　　**重启策略**，当Pod中的容器终止退出后，重启容器的策略。这里的所谓Pod的重启，实际上的做法是容器的重建，之前容器中的数据将会丢失，如果需要持久化数据，那么需要使用数据卷进行持久化设置。Pod支持三种重启策略：Always（默认策略，当容器终止退出后，总是重启容器）、OnFailure（当容器终止且异常退出时，重启）、Never（从不重启）；



### 3.5 Pod生命周期

　　Pod被分配到一个Node上之后，就不会离开这个Node，直到被删除。当某个Pod失败，首先会被Kubernetes清理掉，之后ReplicationController将会在其它机器上（或本机）重建Pod，重建之后Pod的ID发生了变化，那将会是一个新的Pod。所以，Kubernetes中Pod的迁移，实际指的是在新Node上重建Pod。以下给出Pod的生命周期图。

![](D:\Users\pchenc\MD图片\pod生命周期.png)





## 4、Replication Controller

Replication Controller（RC）是Kubernetes中的另一个核心概念，应用托管在Kubernetes之后，Kubernetes需要保证应用能够持续运行，这是RC的工作内容，它会确保任何时间Kubernetes中都有指定数量的Pod在运行。在此基础上，RC还提供了一些更高级的特性，比如滚动升级、升级回滚等。

### 4.1 RC与Pod的关联——Label

　　RC与Pod的关联是通过Label来实现的。Label机制是Kubernetes中的一个重要设计，通过Label进行对象的弱关联，可以灵活地进行分类和选择。对于Pod，需要设置其自身的Label来进行标识，Label是一系列的Key/value对，在Pod-->metadata-->labeks中进行设置。

　　Label的定义是任一的，但是Label必须具有可标识性，比如设置Pod的应用名称和版本号等。另外Lable是不具有唯一性的，为了更准确的标识一个Pod，应该为Pod设置多个维度的label。如下：

　　　　"release" : "stable", "release" : "canary"

　　　　"environment" : "dev", "environment" : "qa", "environment" : "production"

　　　　"tier" : "frontend", "tier" : "backend", "tier" : "cache"

　　　　"partition" : "customerA", "partition" : "customerB"

　　　　"track" : "daily", "track" : "weekly"

　　举例，当你在RC的yaml文件中定义了该RC的selector中的label为app:my-web，那么这个RC就会去关注Pod-->metadata-->labeks中label为app:my-web的Pod。修改了对应Pod的Label，就会使Pod脱离RC的控制。同样，在RC运行正常的时候，若试图继续创建同样Label的Pod，是创建不出来的。因为RC认为副本数已经正常了，再多起的话会被RC删掉的。

### 4.2 弹性伸缩

　　弹性伸缩是指适应负载变化，以弹性可伸缩的方式提供资源。反映到Kubernetes中，指的是可根据负载的高低动态调整Pod的副本数量。调整Pod的副本数是通过修改RC中Pod的副本是来实现的，示例命令如下：

　　扩容Pod的副本数目到10

```
$ kubectl scale relicationcontroller yourRcName --replicas=10
```

　　缩容Pod的副本数目到1

```
$ kubectl scale relicationcontroller yourRcName --replicas=1
```

 

### 4.3 滚动升级

　　滚动升级是一种平滑过渡的升级方式，通过逐步替换的策略，保证整体系统的稳定，在初始升级的时候就可以及时发现、调整问题，以保证问题影响度不会扩大。Kubernetes中滚动升级的命令如下：

```
$ kubectl rolling-update my-rcName-v1 -f my-rcName-v2-rc.yaml --update-period=10s
```

　　升级开始后，首先依据提供的定义文件创建V2版本的RC，然后每隔10s（--update-period=10s）逐步的增加V2版本的Pod副本数，逐步减少V1版本Pod的副本数。升级完成之后，删除V1版本的RC，保留V2版本的RC，及实现滚动升级。

　　升级过程中，发生了错误中途退出时，可以选择继续升级。Kubernetes能够智能的判断升级中断之前的状态，然后紧接着继续执行升级。当然，也可以进行回退，命令如下：

```
$ kubectl rolling-update my-rcName-v1 -f my-rcName-v2-rc.yaml --update-period=10s --rollback
```

回退的方式实际就是升级的逆操作，逐步增加V1.0版本Pod的副本数，逐步减少V2版本Pod的副本数。

### 4.4 新一代副本控制器replica set

　　这里所说的replica set，可以被认为 是“升级版”的Replication Controller。也就是说。replica set也是用于保证与label selector匹配的pod数量维持在期望状态。区别在于，replica set引入了对基于子集的selector查询条件，而Replication Controller仅支持基于值相等的selecto条件查询。这是目前从用户角度肴，两者唯一的显著差异。 社区引入这一API的初衷是用于取代vl中的Replication Controller，也就是说．当v1版本被废弃时，Replication Controller就完成了它的历史使命，而由replica set来接管其工作。虽然replica set可以被单独使用，但是目前它多被Deployment用于进行pod的创建、更新与删除。Deployment在滚动更新等方面提供了很多非常有用的功能，关于DeplOymCn的更多信息，读者们可以在后续小节中获得。



## 5、Deployment

　　Kubernetes提供了一种更加简单的更新RC和Pod的机制，叫做Deployment。通过在Deployment中描述你所期望的集群状态，Deployment Controller会将现在的集群状态在一个可控的速度下逐步更新成你所期望的集群状态。Deployment主要职责同样是为了保证pod的数量和健康，90%的功能与Replication Controller完全一样，可以看做新一代的Replication Controller。但是，它又具备了Replication Controller之外的新特性：

　　　　**Replication Controller全部功能**：Deployment继承了上面描述的Replication Controller全部功能。

　　　　**事件和状态查看**：可以查看Deployment的升级详细进度和状态。

　　　　**回滚**：当升级pod镜像或者相关参数的时候发现问题，可以使用回滚操作回滚到上一个稳定的版本或者指定的版本。

　　　　**版本记录**: 每一次对Deployment的操作，都能保存下来，给予后续可能的回滚使用。

　　　　**暂停和启动**：对于每一次升级，都能够随时暂停和启动。

　　　　**多种升级方案**：Recreate----删除所有已存在的pod,重新创建新的; RollingUpdate----滚动升级，逐步替换的策略，同时滚动升级时，支持更多的附加参数，例如设置最大不可用pod数量，最小升级间隔时间等等。



## 6、Service

　　为了适应快速的业务需求，微服务架构已经逐渐成为主流，微服务架构的应用需要有非常好的服务编排支持。Kubernetes中的核心要素Service便提供了一套简化的服务代理和发现机制，天然适应微服务架构。

### 6.1 原理

　　在Kubernetes中，在受到RC调控的时候，Pod副本是变化的，对于的虚拟IP也是变化的，比如发生迁移或者伸缩的时候。这对于Pod的访问者来说是不可接受的。Kubernetes中的Service是一种抽象概念，它定义了一个Pod逻辑集合以及访问它们的策略，Service同Pod的关联同样是居于Label来完成的。Service的目标是提供一种桥梁， 它会为访问者提供一个固定访问地址，用于在访问时重定向到相应的后端，这使得非 Kubernetes原生应用程序，在无须为Kubemces编写特定代码的前提下，轻松访问后端。

　　Service同RC一样，都是通过Label来关联Pod的。当你在Service的yaml文件中定义了该Service的selector中的label为app:my-web，那么这个Service会将Pod-->metadata-->labeks中label为app:my-web的Pod作为分发请求的后端。当Pod发生变化时（增加、减少、重建等），Service会及时更新。这样一来，Service就可以作为Pod的访问入口，起到代理服务器的作用，而对于访问者来说，通过Service进行访问，无需直接感知Pod。

　　需要注意的是，Kubernetes分配给Service的固定IP是一个虚拟IP，并不是一个真实的IP，在外部是无法寻址的。真实的系统实现上，Kubernetes是通过Kube-proxy组件来实现的虚拟IP路由及转发。所以在之前集群部署的环节上，我们在每个Node上均部署了Proxy这个组件，从而实现了Kubernetes层级的虚拟转发网络。



### 6.2 Service内部负载均衡

　　当Service的Endpoints包含多个IP的时候，及服务代理存在多个后端，将进行请求的负载均衡。默认的负载均衡策略是轮训或者随机（有kube-proxy的模式决定）。同时，Service上通过设置Service-->spec-->sessionAffinity=ClientIP，来实现基于源IP地址的会话保持。



### 6.3 发布Service

　　Service的虚拟IP是由Kubernetes虚拟出来的内部网络，外部是无法寻址到的。但是有些服务又需要被外部访问到，例如web前端。这时候就需要加一层网络转发，即外网到内网的转发。Kubernetes提供了NodePort、LoadBalancer、Ingress三种方式。

　　　　**NodePort**，在之前的Guestbook示例中，已经延时了NodePort的用法。NodePort的原理是，Kubernetes会在每一个Node上暴露出一个端口：nodePort，外部网络可以通过（任一Node）[NodeIP]:[NodePort]访问到后端的Service。

　　　　**LoadBalancer**，在NodePort基础上，Kubernetes可以请求底层云平台创建一个负载均衡器，将每个Node作为后端，进行服务分发。该模式需要底层云平台（例如GCE）支持。

　　　　**Ingress**，是一种HTTP方式的路由转发机制，由Ingress Controller和HTTP代理服务器组合而成。Ingress Controller实时监控Kubernetes API，实时更新HTTP代理服务器的转发规则。HTTP代理服务器有GCE Load-Balancer、HaProxy、Nginx等开源方案。



### 6.4 多个service如何避免地址和端口冲突

　　此处设计思想是，Kubernetes通过为每个service分配一个唯一的ClusterIP，所以当使用ClusterIP：port的组合访问一个service的时候，不管port是什么，这个组合是一定不会发生重复的。另一方面，kube-proxy为每个service真正打开的是一个绝对不会重复的随机端口，用户在service描述文件中指定的访问端口会被映射到这个随机端口上。这就是为什么用户可以在创建service时随意指定访问端口。



## 7、Job

　　从程序的运行形态上来区分，我们可以将Pod分为两类：长时运行服务（jboss、mysql等）和一次性任务（数据计算、测试）。RC创建的Pod都是长时运行的服务，而Job创建的Pod都是一次性任务。

　　在Job的定义中，restartPolicy（重启策略）只能是Never和OnFailure。Job可以控制一次性任务的Pod的完成次数（Job-->spec-->completions）和并发执行数（Job-->spec-->parallelism），当Pod成功执行指定次数后，即认为Job执行完毕。





## 8、业界主流K8S架构

k8s原生的集群监控方案（Heapster+InfluxDB+Grafana)

1. Heapster+InfluxDB+Grafana简介
   heapster是一个监控计算、存储、网络等集群资源的工具，以k8s内置的cAdvisor作为数据源收集集群信息，并汇总出有价值的性能数据(Metrics)：cpu、内存、network、filesystem等，然后将这些数据输出到外部存储(backend)，如InfluxDB，最后再通过相应的UI界面进行可视化展示，如grafana。 另外heapster的数据源和外部存储都是可插拔的，所以可以很灵活的组建出很多监控方案，如：Heapster+ElasticSearch+Kibana等等。



   ![](D:\Users\pchenc\MD图片\heapster.png)

2. 通过dashboard查看集群概况![](D:\Users\pchenc\MD图片\dashboard.png)



![](D:\Users\pchenc\MD图片\dashboard2.png)

3.通过Grafana查看集群详情(cpu、memory、filesystem、network)

![](D:\Users\pchenc\MD图片\grafana.png)


