<template>
	<div id="app">
		<img src="./assets/logo.png">
		<router-view></router-view>
		<modal v-if="showModal">
			<h3 slot="header" class="modal-title">
				Modal title
			</h3>

			<div slot="footer">
				<button type="button" class="btn btn-outline-info" @click="closeModal()">Close </button>
				<button type="button" class="btn btn-primary" data-dismiss="modal" @click="submitAndClose()">Submit</button>
			</div>
		</modal>
		<button type="button" class="btn btn-primary" @click="openModal()">Open Modal</button>
	</div>
</template>

<script>
	import Modal from './components/Modal'

	export default {
		name: 'app',
		components: {
			Modal
		},
		data () {
			return {
				showModal: false
			}
		},
		methods: {
			openModal () {
				this.showModal = true
			},
			closeModal () {
				this.showModal = false
			},
			submitAndClose () {
			}
		}
	}
</script>

<style>
	#app {
		font-family: 'Avenir', Helvetica, Arial, sans-serif;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
		text-align: center;
		color: #2c3e50;
		margin-top: 60px;
	}
</style>
