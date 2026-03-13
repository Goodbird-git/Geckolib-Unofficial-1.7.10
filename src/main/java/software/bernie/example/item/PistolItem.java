package software.bernie.example.item;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Example animated item implementing ISyncable for server-to-client animation sync.
 * Demonstrates the GeckoLib network sync pattern: the server triggers the "firing"
 * animation via GeckoLibNetwork.syncAnimation(), and all clients play it in sync.
 */
public class PistolItem extends Item implements IAnimatable, ISyncable {

	public AnimationFactory factory = GeckoLibUtil.createFactory(this);
	public String controllerName = "controller";
	public static final int ANIM_OPEN = 0;

	public PistolItem() {
		super();
		this.setMaxStackSize(1);
		this.setMaxDamage(201);
		this.setCreativeTab(GeckoLibMod.getGeckolibItemGroup());
		GeckoLibNetwork.registerSyncable(this);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer player, int timeLeft) {
		if (stack.getItemDamage() < (stack.getMaxDamage() - 1)) {
			if (!worldIn.isRemote) {
				EntityArrow arrow = new EntityArrow(worldIn, player, 3.0F);
				arrow.setDamage(2.5);
				arrow.canBePickedUp = 0;

				stack.damageItem(1, player);
				worldIn.spawnEntityInWorld(arrow);

				final int id = GeckoLibUtil.guaranteeIDForStack(stack, (WorldServer) worldIn);
				GeckoLibNetwork.syncAnimation((EntityPlayerMP) player, this, id, ANIM_OPEN);
			}
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
	}

	public <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		return PlayState.CONTINUE;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController(this, controllerName, 1, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public void onAnimationSync(int id, int state) {
		if (state == ANIM_OPEN) {
			final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
			if (controller != null && controller.getAnimationState() == AnimationState.Stopped) {
				controller.markNeedsReload();
				controller.setAnimation(new AnimationBuilder().addAnimation("firing", false));
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return stack;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean hasEffect(ItemStack stack, int pass) {
		return false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
		tooltip.add("\u00a7o" + "Ammo: " + (stack.getMaxDamage() - stack.getItemDamage() - 1) + " / " + (stack.getMaxDamage() - 1));
	}
}
