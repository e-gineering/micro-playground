<template>
	<div class="container">
		<button type="button" class="btn btn-primary" @click="doClick()">Push Here</button>
		<button type="button" class="btn" @click="doReset()">Reset</button>

		<div class="card">
			<code>
				{{ response }}
			</code>
		</div>
	</div>
</template>

<script>
	import { getToken } from '../utils/auth'

	export default {
		name: 'two',
		data () {
			return {
				response: ''
			}
		},
		methods: {
			doClick () {
				const token = getToken()
				const thiz = this

				fetch('http://localhost:8082/servicetwo/resources/two', {
					method: 'GET',
					mode: 'cors',
					headers: new Headers({
						'Content-Type': 'application/json',
						'Authorization': 'Bearer ' + token
					})
				}).then(function (response) {
					if (response.ok) {
						return response.json()
					} else {
						return response
					}
				}).then(function (json) {
					thiz.response = json
				}).catch(function (err) {
					thiz.response = err
					console.log(err)
				})
			},
			doReset () {
				this.response = ''
			}
		}
	}
</script>
