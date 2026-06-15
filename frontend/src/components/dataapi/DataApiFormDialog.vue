<script setup lang="ts">
import { ref, watch } from 'vue'
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

const formRef = ref<any>(null)
const formData = ref<Record<string, any>>({})

const hbaseTypeItems = [
  { title: 'Get', value: 1 },
  { title: 'Scan', value: 2 },
]

const apiTypeLabels: Record<ApiType, string> = {
  hbase: 'HBase',
  solr: 'Solr',
  sql: 'SQL',
}

// Reset form when dialog opens (fires on mount if show is true)
watch(() => props.show, (val) => {
  if (val) {
    formRef.value?.reset()
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
  if (formRef.value?.validate) {
    const { valid } = await formRef.value.validate()
    if (!valid) return
  }

  const payload = {
    ...formData.value,
    dataSourceId: Number(formData.value.dataSourceId),
  }

  await dataapiStore.registerApi(props.apiType, payload as any)

  // Check store error to determine success/failure
  if (dataapiStore.error) {
    appStore.showSnackbar('注册失败', 'error')
  } else {
    appStore.showSnackbar('注册成功', 'success')
    emit('registered')
    emit('close')
  }
}

function onCancel() {
  emit('close')
}
</script>

<template>
  <v-dialog :model-value="show" max-width="640" @update:model-value="(val: boolean) => !val && emit('close')">
    <v-card>
      <v-card-title>注册 {{ apiTypeLabels[apiType] }} API</v-card-title>
      <v-card-text>
        <v-form ref="formRef">
          <!-- Common fields -->
          <v-text-field
            v-model="formData.name"
            label="名称"
            :rules="[(v: string) => !!v || '名称为必填项']"
            required
          />

          <v-text-field
            v-model="formData.comments"
            label="备注"
          />

          <v-text-field
            v-model="formData.dataSourceId"
            label="数据源 ID"
            type="number"
            :rules="[(v: string) => !!v || '数据源 ID 为必填项']"
            required
          />

          <!-- HBase fields -->
          <template v-if="apiType === 'hbase'">
            <v-select
              v-model="formData.type"
              :items="hbaseTypeItems"
              item-title="title"
              item-value="value"
              label="类型"
              :rules="[(v: number | null) => v != null || '类型为必填项']"
              required
            />
            <v-text-field
              v-model="formData.tableName"
              label="表名"
              :rules="[(v: string) => !!v || '表名为必填项']"
              required
            />
            <v-text-field
              v-model="formData.columns"
              label="列"
              :rules="[(v: string) => !!v || '列为必填项']"
              required
            />
          </template>

          <!-- Solr fields -->
          <template v-if="apiType === 'solr'">
            <v-text-field
              v-model="formData.collection"
              label="Collection"
              :rules="[(v: string) => !!v || 'Collection 为必填项']"
              required
            />
            <v-text-field
              v-model="formData.fields"
              label="字段"
              :rules="[(v: string) => !!v || '字段为必填项']"
              required
            />
            <v-text-field
              v-model="formData.conditions"
              label="查询条件"
            />
            <v-text-field
              v-model="formData.orders"
              label="排序"
            />
          </template>

          <!-- SQL fields -->
          <template v-if="apiType === 'sql'">
            <v-textarea
              v-model="formData.sql"
              label="SQL"
              :rules="[(v: string) => !!v || 'SQL 为必填项']"
              required
            />

            <v-switch
              v-model="formData.pageTag"
              :true-value="1"
              :false-value="0"
              label="分页"
            />

            <v-text-field
              v-if="formData.pageTag === 1"
              v-model.number="formData.pageSize"
              label="每页大小"
              type="number"
            />

            <!-- SQL paramList editor -->
            <div class="mt-4">
              <div class="text-subtitle-2 mb-2">参数列表</div>
              <div
                v-for="(param, index) in formData.paramList"
                :key="index"
                class="d-flex align-center ga-2 mb-2"
              >
                <v-text-field
                  v-model="param.name"
                  label="参数名"
                  density="compact"
                  hide-details
                />
                <v-select
                  v-model="param.type"
                  :items="['STRING', 'NUMERIC']"
                  label="类型"
                  density="compact"
                  hide-details
                  style="width: 130px; flex-shrink: 0;"
                />
                <v-text-field
                  v-model="param.description"
                  label="描述"
                  density="compact"
                  hide-details
                  class="flex-grow-1"
                />
                <v-btn
                  icon="mdi-close-circle"
                  variant="text"
                  size="small"
                  color="error"
                  @click="removeParam(index as number)"
                  class="flex-shrink-0"
                />
              </div>
              <v-btn
                variant="text"
                size="small"
                prepend-icon="mdi-plus"
                @click="addParam"
              >
                添加参数
              </v-btn>
            </div>
          </template>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="onCancel">取消</v-btn>
        <v-btn color="primary" variant="text" @click="onSubmit">确认</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
