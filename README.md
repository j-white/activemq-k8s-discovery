## Kubernetes Discovery for ActiveMQ

### Building

Clone the repository and build the discovery agent using Maven:

    mvn clean package

Once complete, copy the `target/activemq-k8s-discovery-*-jar-with-dependencies.jar` into the class-path of your ActiveMQ install.

### Configuring

Here's an example snippet that can be added to your `activemq.xml`:

```xml
<networkConnector uri="k8s://default?podLabelKey=app&amp;podLabelValue=activemq"
            dynamicOnly="true"
            networkTTL="3"
            prefetchSize="1"
            decreaseNetworkConsumerPriority="true" />
</networkConnectors>
```

Here are the available options, these can be set using query parameters in the URI above:

| Option           | Default Value  | Description                                |
| ---------------- |:--------------:|:------------------------------------------:|
| namespace        | default        | Kubernetes namespace                       |
| podLabelKey      | app            | Pod label key to match                     |
| podLabelValue    | activemq       | Pod label value to match                   |
| serviceUrlFormat | tcp://%s:61616 | URL used, %s is replaced with the pod's IP |
| sleepDelay       | 30000          | Delay in milliseconds between polls        |

### Deploying

Here are some notes I used for testing the solution. I used to the Docker image from https://github.com/j-white/activemq.

Fire up a Kubernetes cluster and configure the local Docker environment to use the cluster:

    minikube start --cpus=4 --memory=6000
    eval $(minikube docker-env)

Now build the Docker image:

    docker build -t cloud-activemq:1.0.0 .

Create the deployment and service:

    kubectl create -f activemq-deployment.yaml --record
    kubectl create -f activemq-service.yaml --record
    kubectl get deployments
    kubectl get services

Determine the IP address and port of the exposed service:

    minikube ip
    kubectl describe service activemq | grep NodePort

Peek at the logs:

    kubectl exec activemq-deployment-1647995871-8q3vf -- tail -f /var/log/supervisor/activemq.log

Access the ActiveMQ Web UI:

    kubectl port-forward activemq-deployment-1647995871-8q3vf 8161:8161

Scale up:

    kubectl scale --replicas=6 deployment/activemq-deployment

Upgrade the deployment:

    kubectl set image deployment/activemq-deployment activemq=cloud-activemq:1.0.1
    kubectl rollout status deployment/activemq-deployment
    kubectl get deployments
