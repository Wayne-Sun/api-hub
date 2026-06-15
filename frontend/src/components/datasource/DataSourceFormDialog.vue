<script setup lang="ts">
import { ref, watch } from 'vue'
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

const formRef = ref<any>(null)
const formData = ref<Record<string, any>>({})

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
    await store.createSource(props.sourceType, formData.value as HbaseSourceConf | SolrSourceConf | SqlSourceConf)
    emit('created')
    emit('close')
  } catch {
    // Error is handled by store
  }
}

function onCancel() {
  emit('close')
}
</script>

<template>
  <v-dialog :model-value="show" max-width="600" @update:model-value="(val: boolean) => !val && emit('close')">
    <v-card>
      <v-card-title>{{ sourceType === 'hbase' ? 'HBase' : sourceType === 'solr' ? 'Solr' : 'SQL' }} 数据源</v-card-title>
      <v-card-text>
        <v-form ref="formRef">
          <v-text-field v-model="formData.name" label="名称" :rules="[(v: string) => !!v || '名称为必填项']" required />

          <v-text-field v-model="formData.comments" label="备注" />

          <!-- HBase fields -->
          <template v-if="sourceType === 'hbase'">
            <v-text-field v-model="formData.hbaseSitePath" label="HBase 配置路径" :rules="[(v: string) => !!v || '配置路径为必填项']" required />
            <v-text-field v-model="formData.coreSitePath" label="Core 配置路径" :rules="[(v: string) => !!v || '配置路径为必填项']" required />
          </template>

          <!-- Solr fields -->
          <template v-if="sourceType === 'solr'">
            <v-text-field v-model="formData.zkHosts" label="ZooKeeper 地址" :rules="[(v: string) => !!v || 'ZooKeeper 地址为必填项']" required />
            <v-text-field v-model="formData.zkChroot" label="Chroot 路径" :rules="[(v: string) => !!v || 'Chroot 路径为必填项']" required />
          </template>

          <!-- SQL fields -->
          <template v-if="sourceType === 'sql'">
            <v-select v-model="formData.dialect" label="数据库类型" :items="['MYSQL', 'ORACLE']" :rules="[(v: string) => !!v || '数据库类型为必填项']" required />
            <v-text-field v-model="formData.url" label="连接地址" :rules="[(v: string) => !!v || '连接地址为必填项']" required />
            <v-text-field v-model="formData.username" label="用户名" :rules="[(v: string) => !!v || '用户名为必填项']" required />
            <v-text-field v-model="formData.password" label="密码" type="password" :rules="[(v: string) => !!v || '密码为必填项']" required />
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
