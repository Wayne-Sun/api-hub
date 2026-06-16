<script setup lang="ts">
import { ref, watch } from 'vue'
import { NModal, NForm, NFormItem, NInput, NSelect, NButton, type FormInst, type FormRules } from 'naive-ui'
import { useDatasourceStore } from '@/stores/datasource'
import type { SourceType, HbaseSourceConf, SolrSourceConf, SqlSourceConf } from '@/types'

const props = defineProps<{
  show: boolean
  sourceType: SourceType
}>()

const emit = defineEmits<{
  close: []
  created: []
}>()

const store = useDatasourceStore()

const formRef = ref<FormInst | null>(null)
const formData = ref<Record<string, any>>({})

const formRules: FormRules = {
  name: [
    { required: true, message: '名称为必填项', trigger: ['blur', 'input'] }
  ],
  hbaseSitePath: [
    { required: true, message: '配置路径为必填项', trigger: ['blur', 'input'] }
  ],
  coreSitePath: [
    { required: true, message: '配置路径为必填项', trigger: ['blur', 'input'] }
  ],
  zkHosts: [
    { required: true, message: 'ZooKeeper 地址为必填项', trigger: ['blur', 'input'] }
  ],
  zkChroot: [
    { required: true, message: 'Chroot 路径为必填项', trigger: ['blur', 'input'] }
  ],
  dialect: [
    { required: true, message: '数据库类型为必填项', trigger: ['blur', 'change'] }
  ],
  url: [
    { required: true, message: '连接地址为必填项', trigger: ['blur', 'input'] }
  ],
  username: [
    { required: true, message: '用户名为必填项', trigger: ['blur', 'input'] }
  ],
  password: [
    { required: true, message: '密码为必填项', trigger: ['blur', 'input'] }
  ]
}

// Reset form when dialog opens
watch(() => props.show, (val) => {
  if (val) {
    formData.value = { name: '', comments: '' }
    if (props.sourceType === 'hbase') {
      formData.value.hbaseSitePath = ''
      formData.value.coreSitePath = ''
    } else if (props.sourceType === 'solr') {
      formData.value.zkHosts = ''
      formData.value.zkChroot = ''
    } else if (props.sourceType === 'sql') {
      formData.value.dialect = 'MYSQL'
      formData.value.url = ''
      formData.value.username = ''
      formData.value.password = ''
    }
  }
})

async function onSubmit() {
  try {
    await formRef.value?.validate()
    await store.createSource(props.sourceType, formData.value as HbaseSourceConf | SolrSourceConf | SqlSourceConf)
    emit('created')
    emit('close')
  } catch {
    // Error is handled by store / validation error caught here
  }
}

function onCancel() {
  emit('close')
}
</script>

<template>
  <n-modal :show="show" preset="card" :title="`${sourceType === 'hbase' ? 'HBase' : sourceType === 'solr' ? 'Solr' : 'SQL'} 数据源`" :style="{ maxWidth: '600px' }" @update:show="(v: boolean) => !v && emit('close')">
    <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="top">
      <n-form-item path="name" label="名称">
        <n-input v-model:value="formData.name" />
      </n-form-item>

      <n-form-item path="comments" label="备注">
        <n-input v-model:value="formData.comments" />
      </n-form-item>

      <!-- HBase fields -->
      <template v-if="sourceType === 'hbase'">
        <n-form-item path="hbaseSitePath" label="HBase 配置路径">
          <n-input v-model:value="formData.hbaseSitePath" />
        </n-form-item>
        <n-form-item path="coreSitePath" label="Core 配置路径">
          <n-input v-model:value="formData.coreSitePath" />
        </n-form-item>
      </template>

      <!-- Solr fields -->
      <template v-if="sourceType === 'solr'">
        <n-form-item path="zkHosts" label="ZooKeeper 地址">
          <n-input v-model:value="formData.zkHosts" />
        </n-form-item>
        <n-form-item path="zkChroot" label="Chroot 路径">
          <n-input v-model:value="formData.zkChroot" />
        </n-form-item>
      </template>

      <!-- SQL fields -->
      <template v-if="sourceType === 'sql'">
        <n-form-item path="dialect" label="数据库类型">
          <n-select v-model:value="formData.dialect" :options="[{ label: 'MYSQL', value: 'MYSQL' }, { label: 'ORACLE', value: 'ORACLE' }]" />
        </n-form-item>
        <n-form-item path="url" label="连接地址">
          <n-input v-model:value="formData.url" />
        </n-form-item>
        <n-form-item path="username" label="用户名">
          <n-input v-model:value="formData.username" />
        </n-form-item>
        <n-form-item path="password" label="密码">
          <n-input v-model:value="formData.password" type="password" />
        </n-form-item>
      </template>
    </n-form>
    <template #footer>
      <div style="flex: 1" />
      <n-button @click="onCancel">取消</n-button>
      <n-button type="primary" @click="onSubmit">确认</n-button>
    </template>
  </n-modal>
</template>
