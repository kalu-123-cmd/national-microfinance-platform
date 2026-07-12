{{/*
Expand the name of the chart.
*/}}
{{- define "microfinance.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "microfinance.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "microfinance.labels" -}}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}

{{/*
Selector labels for a service
Usage: {{ include "microfinance.selectorLabels" (dict "app" "auth-service") }}
*/}}
{{- define "microfinance.selectorLabels" -}}
app.kubernetes.io/name: {{ .app }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Service Deployment template
Usage:
{{ include "microfinance.serviceDeployment" (dict
    "name"     "auth-service"
    "port"     8081
    "replicas" 2
    "Values"   .Values
    "Release"  .Release
    "Chart"    .Chart ) }}
*/}}
{{- define "microfinance.serviceDeployment" -}}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .name }}
  labels:
    app: {{ .name }}
    {{- include "microfinance.labels" . | nindent 4 }}
spec:
  replicas: {{ .replicas | default 2 }}
  selector:
    matchLabels:
      app: {{ .name }}
  template:
    metadata:
      labels:
        app: {{ .name }}
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: {{ .port | quote }}
        prometheus.io/path: "/actuator/prometheus"
    spec:
      initContainers:
        - name: wait-for-discovery
          image: curlimages/curl:8.6.0
          command: ['sh','-c','until curl -sf http://discovery-server:8761/actuator/health; do sleep 5; done']
      containers:
        - name: {{ .name }}
          image: {{ .Values.image.registry }}/{{ .name }}:{{ .Values.image.tag }}
          imagePullPolicy: {{ .Values.image.pullPolicy }}
          ports:
            - containerPort: {{ .port }}
          envFrom:
            - configMapRef:
                name: microfinance-config
            - secretRef:
                name: microfinance-secrets
          resources:
            {{- toYaml .Values.resources.core | nindent 12 }}
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: {{ .port }}
            initialDelaySeconds: 45
            periodSeconds: 10
            failureThreshold: 3
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: {{ .port }}
            initialDelaySeconds: 90
            periodSeconds: 15
            failureThreshold: 3
---
apiVersion: v1
kind: Service
metadata:
  name: {{ .name }}
  labels:
    app: {{ .name }}
spec:
  selector:
    app: {{ .name }}
  ports:
    - port: {{ .port }}
      targetPort: {{ .port }}
{{- end }}
