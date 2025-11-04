$(function(){

// User Register validation

	var $userRegister=$("#userRegister");

	$userRegister.validate({
		
		rules:{
			name:{
				required:true,
				lettersonly:true
			}
			,
			email: {
				required: true,
				space: true,
				email: true
			},
			mobileNumber: {
				required: true,
				space: true,
				numericOnly: true,
<<<<<<< HEAD
				minlength: 10,
				maxlength: 12
=======
>>>>>>> vinh

			},
			password: {
				required: true,
				space: true

			},
			confirmpassword: {
				required: true,
				space: true,
				equalTo: '#pass'

			},
			address: {
				required: true,
				all: true

<<<<<<< HEAD
			},

			city: {
				required: true,
				space: true

			},
			state: {
				required: true,


			},
			pincode: {
				required: true,
				space: true,
				numericOnly: true

			}, img: {
				required: true,
=======
>>>>>>> vinh
			}
			
		},
		messages:{
			name:{
<<<<<<< HEAD
				required:'name required',
				lettersonly:'invalid name'
			},
			email: {
				required: 'email name must be required',
				space: 'space not allowed',
				email: 'Invalid email'
			},
			mobileNumber: {
				required: 'mob no must be required',
				space: 'space not allowed',
				numericOnly: 'invalid mob no',
				minlength: 'min 10 digit',
				maxlength: 'max 12 digit'
			},

			password: {
				required: 'password must be required',
				space: 'space not allowed'

			},
			confirmpassword: {
				required: 'confirm password must be required',
				space: 'space not allowed',
				equalTo: 'password mismatch'

			},
			address: {
				required: 'address must be required',
				all: 'invalid'

			},

			city: {
				required: 'city must be required',
				space: 'space not allowed'

			},
			state: {
				required: 'state must be required',
				space: 'space not allowed'

			},
			pincode: {
				required: 'pincode must be required',
				space: 'space not allowed',
				numericOnly: 'invalid pincode'

			},
			img: {
				required: 'image required',
=======
				required:'Yêu cầu điền',
				lettersonly:'Tên không hợp lệ'
			},
			email: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng',
				email: 'Email không hợp lệ'
			},
			mobileNumber: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng',
				numericOnly: 'Số điện thoại không hợp lệ',
			},

			password: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng'

			},
			confirmpassword: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng',
				equalTo: 'Không trùng khớp'

			},
			address: {
				required: 'Yêu cầu điền',
				all: 'Không hợp lệ'

>>>>>>> vinh
			}
		}
	})
	
	
// Orders Validation

var $orders=$("#orders");

$orders.validate({
		rules:{
			firstName:{
				required:true,
				lettersonly:true
			},
			lastName:{
				required:true,
				lettersonly:true
			}
			,
			email: {
				required: true,
				space: true,
				email: true
			},
			mobileNo: {
				required: true,
				space: true,
				numericOnly: true,
				minlength: 10,
				maxlength: 12

			},
			address: {
				required: true,
				all: true

			},
<<<<<<< HEAD

			city: {
				required: true,
				space: true

			},
			state: {
				required: true,


			},
			pincode: {
				required: true,
				space: true,
				numericOnly: true

			},
=======
>>>>>>> vinh
			paymentType:{
			required: true
			}
		},
		messages:{
			firstName:{
<<<<<<< HEAD
				required:'first required',
				lettersonly:'invalid name'
			},
			lastName:{
				required:'last name required',
				lettersonly:'invalid name'
			},
			email: {
				required: 'email name must be required',
				space: 'space not allowed',
				email: 'Invalid email'
			},
			mobileNo: {
				required: 'mob no must be required',
				space: 'space not allowed',
				numericOnly: 'invalid mob no',
				minlength: 'min 10 digit',
				maxlength: 'max 12 digit'
			}
		   ,
			address: {
				required: 'address must be required',
				all: 'invalid'

			},

			city: {
				required: 'city must be required',
				space: 'space not allowed'

			},
			state: {
				required: 'state must be required',
				space: 'space not allowed'

			},
			pincode: {
				required: 'pincode must be required',
				space: 'space not allowed',
				numericOnly: 'invalid pincode'

			},
			paymentType:{
			required: 'select payment type'
=======
				required:'Yêu cầu điền',
				lettersonly:'Không hợp lệ'
			},
			lastName:{
				required:'Yêu cầu điền',
				lettersonly:'Không hợp lệ'
			},
			email: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng',
				email: 'Không hợp lệ'
			},
			mobileNo: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng',
				numericOnly: 'Không hợp lệ',
			}
		   ,
			address: {
				required: 'Yêu cầu điền',
				all: 'Không hợp lệ'

			},
			paymentType:{
			required: 'Chọn phương thức thanh toán'
>>>>>>> vinh
			}
		}	
})

	
})



jQuery.validator.addMethod('lettersonly', function(value, element) {
		return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
	});
	
		jQuery.validator.addMethod('space', function(value, element) {
		return /^[^-\s]+$/.test(value);
	});

	jQuery.validator.addMethod('all', function(value, element) {
		return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
	});


	jQuery.validator.addMethod('numericOnly', function(value, element) {
		return /^[0-9]+$/.test(value);
	});