<script setup lang="ts">
import { ref, watch } from 'vue'
import { NModal, NForm, NFormItem, NInput, NSelect, NSwitch, NButton, NIcon, type FormInst, type FormRules } from 'naive-ui'
import { CloseCircleOutline, AddOutline } from '@vicons/ionicons5'
import { useDataapiStore } from '@/stores/dataapi'
import { useAppStore } from '@/stores/app'
import type { ApiType, SqlParam } from '@/types'

const props = defineProps<{
  show: boolean
  apiType: ApiType
}>()

const emit = defineEmits<{
  close: []
  registered: []
}>()

const dataapiStore = useDataapiStore()
const appStore = useAppStore()

const formRef = ref<FormInst | null>(null)
const formData = ref<Record<string, any>>({})

const formRules: FormRules = {
  name: [
    { required: true, message: '名称为必填项', trigger: ['blur', 'input'] }
  ],
  dataSourceId: [
    { required: true, message: '数据源 ID 为必填项', trigger: ['blur', 'input'] }
  ],
  tableName: [
    { required: true, message: '表名为必填项', trigger: ['blur', 'input'] }
  ],
  columns: [
    { required: true, message: '列为必填项', trigger: ['blur', 'input'] }
  ],
  collection: [
    { required: true, message: 'Collection 为必填项', trigger: ['blur', 'input'] }
  ],
  fields: [
    { required: true, message: '字段为必填项', trigger: ['blur', 'input'] }
  ],
  sql: [
    { required: true, message: 'SQL 为必填项', trigger: ['blur', 'input'] }
  ],
}

const hbaseTypeItems = [
  { label: 'Get', value: 1 },
  { label: 'Scan', value: 2 },
]

const apiTypeLabels: Record<ApiType, string> = {
  hbase: 'HBase',
  solr: 'Solr',
  sql: 'SQL',
}

// Reset form when dialog opens (fires on mount if show is true)
watch(() => props.show, (val) => {
  if (val) {
    formData.value = {
      name: '',
      comments: '',
      dataSourceId: '',
    }
    if (props.apiType === 'hbase') {
      formData.value.type = 1
      formData.value.tableName = ''
      formData.value.columns = ''
    } else if (props.apiType === 'solr') {
      formData.value.collection = ''
      formData.value.fields = ''
      formData.value.conditions = ''
      formData.value.orders = ''
    } else if (props.apiType === 'sql') {
      formData.value.sql = ''
      formData.value.paramList = [] as SqlParam[]
      formData.value.pageTag = 0
      formData.value.pageSize = undefined
    }
  }
})

function addParam() {
  formData.value.paramList.push({
    name: '',
    type: 'STRING',
    description: '',
  })
}

function removeParam(index: number) {
  formData.value.paramList.splice(index, 1)
}

async function onSubmit() {
  try {
    await formRef.value?.validate()
    const payload = {
      ...formData.value,
      dataSourceId: Number(formData.value.dataSourceId),
    }
    await dataapiStore.registerApi(props.apiType, payload as any)
    if (dataapiStore.error) {
      appStore.showSnackbar('注册失败', 'error')
    } else {
      appStore.showSnackbar('注册成功', 'success')
      emit('registered')
      emit('close')
    }
  } catch {
    // validation error caught here
  }
}

function onCancel() {
  emit('close')
}
</script>

<template>
  <n-modal :show="show" preset="card" :title="`注册 ${apiTypeLabels[apiType]} API`" :style="{ maxWidth: '640px' }" @update:show="(v: boolean) => !v && emit('close')">
    <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="top">
      <!-- Common fields -->
      <n-form-item path="name" label="名称">
        <n-input v-model:value="formData.name" />
      </n-form-item>

      <n-form-item path="comments" label="备注">
        <n-input v-model:value="formData.comments" />
      </n-form-item>

      <n-form-item path="dataSourceId" label="数据源 ID">
        <n-input v-model:value="formData.dataSourceId" />
      </n-form-item>

      <!-- HBase fields -->
      <template v-if="apiType === 'hbase'">
        <n-form-item path="type" label="类型">
          <n-select v-model:value="formData.type" :options="hbaseTypeItems" />
        </n-form-item>
        <n-form-item path="tableName" label="表名">
          <n-input v-model:value="formData.tableName" />
        </n-form-item>
        <n-form-item path="columns" label="列">
          <n-input v-model:value="formData.columns" />
        </n-form-item>
      </template>

      <!-- Solr fields -->
      <template v-if="apiType === 'solr'">
        <n-form-item path="collection" label="Collection">
          <n-input v-model:value="formData.collection" />
        </n-form-item>
        <n-form-item path="fields" label="字段">
          <n-input v-model:value="formData.fields" />
        </n-form-item>
        <n-form-item path="conditions" label="查询条件">
          <n-input v-model:value="formData.conditions" />
        </n-form-item>
        <n-form-item path="orders" label="排序">
          <n-input v-model:value="formData.orders" />
        </n-form-item>
      </template>

      <!-- SQL fields -->
      <template v-if="apiType === 'sql'">
        <n-form-item path="sql" label="SQL">
          <n-input v-model:value="formData.sql" type="textarea" :autosize="{ minRows: 3, maxRows: 8 }" />
        </n-form-item>

        <n-form-item path="pageTag" label="分页">
          <n-switch v-model:value="formData.pageTag" :checked-value="1" :unchecked-value="0" />
        </n-form-item>

        <n-form-item v-if="formData.pageTag === 1" path="pageSize" label="每页大小">
          <n-input v-model:value="formData.pageSize" />
        </n-form-item>

        <!-- SQL paramList editor -->
        <div style="margin-top: 16px;">
          <div style="font-size: 14px; font-weight: 500; margin-bottom: 8px;">参数列表</div>
          <div
            v-for="(param, index) in formData.paramList"
            :key="index"
            style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px;"
          >
            <n-input v-model:value="param.name" placeholder="参数名" size="small" style="flex: 1;" />
            <n-select v-model:value="param.type" :options="[{ label: 'STRING', value: 'STRING' }, { label: 'NUMERIC', value: 'NUMERIC' }]" size="small" style="width: 130px; flex-shrink: 0;" />
            <n-input v-model:value="param.description" placeholder="描述" size="small" style="flex: 1;" />
            <n-button quaternary circle size="small" @click="removeParam(index as number)">
              <n-icon><CloseCircleOutline /></n-icon>
            </n-button>
          </div>
          <n-button size="small" @click="addParam">
            <n-icon><AddOutline /></n-icon>
            添加参数
          </n-button>
        </div>
      </template>
    </n-form>
    <template #footer>
      <div style="flex: 1" />
      <n-button @click="onCancel">取消</n-button>
      <n-button type="primary" @click="onSubmit">确认</n-button>
    </template>
  </n-modal>
</template>
