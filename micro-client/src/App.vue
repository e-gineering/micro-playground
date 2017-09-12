<template>
	<div id="app">
		<modal v-if="!authenticated">
			<h3 slot="header" class="modal-title">
				Please sign in
			</h3>

			<div slot="body">
				<div class="form-group">
					<label for="username">Username</label>
					<input type="text" id="username" class="form-control" placeholder="Username" autofocus="true" v-model="username" />
				</div>
				<div class="form-group">
					<label for="password">Password</label>
					<input type="password" id="password" class="form-control" placeholder="Password" v-model="password" />
				</div>
			</div>

			<div slot="footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" @click="submitAndClose()">Submit</button>
			</div>
		</modal>

		<div v-if="authenticated">
			<nav class="navbar navbar-default">
				<div class="container-fluid">
					<div class="navbar-header">
						<router-link to="/" class="navbar-brand">
							<img src="./assets/logo.png" width="24" height="24">
						</router-link>
					</div>

					<div class="collapse navbar-collapse">
						<ul class="nav navbar-nav navbar-right">
							<li>
								<button class="btn btn-danger log" @click="handleLogout()">Log out</button>
							</li>
						</ul>
					</div>
				</div>
			</nav>

			<div class="container">
				<ul class="nav nav-tabs">
					<li role="presentation"><router-link to="/">Home</router-link></li>
					<li role="presentation"><router-link to="/one">One</router-link></li>
					<li role="presentation"><router-link to="/two">Two</router-link></li>
				</ul>

				<router-view></router-view>
			</div>
		</div>
	</div>
</template>

<script>
	import Modal from './components/Modal'
	import decode from 'jwt-decode'

	const TOKEN_KEY = 'micro-token'

	export default {
		name: 'app',
		components: {
			Modal
		},
		data () {
			return {
				authenticated: false,
				username: '',
				password: ''
			}
		},
		methods: {
			submitAndClose () {
				if (this.username && this.password) {
					var thiz = this

					fetch('http://localhost:8080/security/resources/authentication/login', {
						method: 'POST',
						mode: 'cors',
						headers: new Headers({
							'Content-Type': 'application/json'
						}),
						body: JSON.stringify({
							username: thiz.username,
							password: thiz.password
						})
					}).then(function (raw) {
						return raw.json()
					}).then(function (json) {
						var token = json.token

						thiz.setToken(token)

						thiz.authenticated = thiz.isAuthenticated()
						thiz.username = ''
						thiz.password = ''
					}).catch(function (err) {
						console.log(err)
					})
				}
			},
			handleLogout () {
				this.clearToken()
				this.authenticated = this.isAuthenticated()
			},
			getToken () {
				return localStorage.getItem(TOKEN_KEY)
			},
			setToken (token) {
				localStorage.setItem(TOKEN_KEY, token)
			},
			clearToken () {
				localStorage.removeItem(TOKEN_KEY)
			},
			isAuthenticated: function () {
				const rawToken = this.getToken()

				if (!rawToken) {
					return false
				}

				const token = decode(rawToken)

				if (!token.exp) {
					return false
				}

				const date = new Date(0)
				date.setUTCSeconds(token.exp)

				const now = new Date()

				return (date >= now)
			}
		}
	}
</script>

<style>
	.log {
		margin: 6px 10px 0 0;
	}
</style>
