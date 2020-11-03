#
# generate CRDs for Tekton Pipelines
#
rm -rf pipeline 2>/dev/null
git clone -b v0.17.2 https://github.com/tektoncd/pipeline
cd pipeline
#
# update Go code so that controller-gen generates the proper CRD format
#
find pkg -type f -exec sed -i '' -e "s/\/\/ +k8s:deepcopy-gen:interfaces=k8s.io\/apimachinery\/pkg\/runtime.Object/\/\/ +k8s:deepcopy-gen:interfaces=k8s.io\/apimachinery\/pkg\/runtime.Object\n\/\/ +kubebuilder:storageversion/g" {} \;
go get -v -u sigs.k8s.io/controller-tools/cmd/controller-gen@v0.4.0
export PATH=$PATH:$HOME/go/bin
controller-gen crd paths="./pkg/apis/...;k8s.io/apimachinery/pkg/apis/meta/v1/..." output:dir=../crds crd:crdVersions=v1beta1,trivialVersions=true
#
# patch type: Any as it is not supported by the IntelliJ Kubernetes plugin
#
sed -i '' -f ../type.rule ../crds/tekton*yaml
cd ..
#
# generate CRDs for Tekton Triggers
#
rm -rf triggers 2>/dev/null
git clone -b v0.9.1 https://github.com/tektoncd/triggers
cd triggers
#
# update Go code so that controller-gen generates the proper CRD format
#
find pkg -type f -exec sed -i '' -e "s/\/\/ +k8s:deepcopy-gen:interfaces=k8s.io\/apimachinery\/pkg\/runtime.Object/\/\/ +k8s:deepcopy-gen:interfaces=k8s.io\/apimachinery\/pkg\/runtime.Object\n\/\/ +kubebuilder:storageversion/g" {} \;
go mod vendor
GOFLAGS="-mod=vendor" controller-gen crd paths="./pkg/apis/..." output:dir=../crds crd:crdVersions=v1beta1,trivialVersions=true
#
# patch type: Any as it is not supported by the IntelliJ Kubernetes plugin
#
sed -i '' -f ../type.rule ../crds/triggers*yaml
